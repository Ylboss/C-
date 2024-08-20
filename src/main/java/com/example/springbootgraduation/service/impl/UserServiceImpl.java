package com.example.springbootgraduation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootgraduation.common.Constants;
import com.example.springbootgraduation.common.ValidationEnum;
import com.example.springbootgraduation.entity.dto.UserDTO;
import com.example.springbootgraduation.entity.dto.UserPasswordDTO;
import com.example.springbootgraduation.entity.Menu;
import com.example.springbootgraduation.entity.User;
import com.example.springbootgraduation.entity.Validation;
import com.example.springbootgraduation.exception.ServiceException;
import com.example.springbootgraduation.mapper.RoleMapper;
import com.example.springbootgraduation.mapper.RoleMenuMapper;
import com.example.springbootgraduation.mapper.UserMapper;
import com.example.springbootgraduation.service.IMenuService;
import com.example.springbootgraduation.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootgraduation.service.IValidationService;
import com.example.springbootgraduation.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yl
 * @since 2024-04-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    // 用于打印错误日志
    private static final Log LOG = Log.get();

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param userDTO 用户类
     * @return 返回登录结果
     */
    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one != null) {
            BeanUtil.copyProperties(one, userDTO, true);
            // 设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);
            String role = one.getRole(); // ROLE_ADMIN
            // 设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);
            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_1000, "用户名或密码错误");
        }
    }

    /**
     * 获取当前角色的菜单列表
     *
     * @param roleFlag 当前角色唯一标识
     * @return 返回当前角色的菜单列表
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        // 当前角色所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        // 查出系统所有的菜单
        List<Menu> menus = menuService.findMenus("");
        // new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        // 筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            // removeIf 移除 children 里面不在 menuIds集合中的元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }

    /**
     * 用户注册
     *
     * @param userDTO 用户类
     * @return 返回注册结果
     */
    @Override
    public User register(UserDTO userDTO) {
        // 根据username查询用户信息
        User existingUser = getUserInfoByUsername(userDTO.getUsername());

        if (existingUser != null) {
            throw new ServiceException(Constants.CODE_1000, "用户名已存在");
        }

        User newUser = new User();
        BeanUtil.copyProperties(userDTO, newUser, true);
        save(newUser);

        return newUser;
    }

    // 根据username查询用户信息
    private User getUserInfoByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 判断当前用户是否存在
     *
     * @param userDTO 待判断用户
     * @return 判断结果
     */
    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper);
        } catch (Exception e) {
            // 在控制台打印错误日志
            LOG.error(e);
            throw new ServiceException(Constants.CODE_1000, "系统错误");
        }
        return one;
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int update = userMapper.updatePassword(userPasswordDTO);
        if (update < 1) {
            throw new ServiceException(Constants.CODE_1000, "密码错误");
        }
    }

    // 不要忘记导入相关的依赖和配置
    @Autowired
    JavaMailSender javaMailSender;

    @Resource
    private IValidationService validationService;
    @Value("${spring.mail.username}")
    private String from;

    // 邮箱验证
    @Override
    public UserDTO loginEmail(UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();

        // 先查询 邮箱验证的表，看看之前有没有发送过  邮箱code，如果不存在，就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", email);
        validationQueryWrapper.eq("code", code);
        validationQueryWrapper.ge("time", new Date());  // 查询数据库没过期的code, where time >= new Date()
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码过期，请重新获取");
        }

        // 如果验证通过了
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);  //存根据email查询用户信息
        User user = getOne(userQueryWrapper);

        if (user == null) {
            throw new ServiceException("-1", "未找到用户");
        }

        BeanUtil.copyProperties(user, userDTO, true);
        // 设置token
        String token = TokenUtils.genToken(user.getId().toString(), user.getPassword());
        userDTO.setToken(token);

        String role = user.getRole();
        // 设置用户的菜单列表
        List<Menu> roleMenus = getRoleMenus(role);
        userDTO.setMenus(roleMenus);
        return userDTO;
    }

    // 发送邮箱验证码
    @Override
    public void sendEmailCode(String email, Integer type) throws MessagingException {
        Date now = new Date();
        // 先查询同类型code
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", email);
        validationQueryWrapper.eq("type", type);
        validationQueryWrapper.ge("time", now);  // 查询数据库没过期的code
        Validation validation = validationService.getOne(validationQueryWrapper);
        if (validation != null) {
            throw new ServiceException("-1", "当前您的验证码仍然有效，请不要重复发送");
        }


        String code = RandomUtil.randomNumbers(4); // 随机一个 4位长度的验证码

        if (ValidationEnum.LOGIN.getCode().equals(type)) {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(from);  // 发送人
            message.setTo(email);
            message.setSentDate(now);
            message.setSubject("【XUNYUYU】登录邮箱验证");
            message.setText("您本次登录的验证码是：" + code + "，有效期5分钟。请妥善保管，切勿泄露");
            javaMailSender.send(message);
        } else if (ValidationEnum.FORGET_PASS.getCode().equals(type)){
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setSubject("【XUNYUYU】忘记密码验证");
            helper.setFrom(from);  // 发送人
            helper.setTo(email);
            helper.setSentDate(now);  // 富文本
            String context="<b>尊敬的用户：</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好，您本次忘记密码的验证码是："+
                    "<b color=\"'red'\">"  + code + "</b><br>"
                    +"，有效期5分钟。请妥善保管，切勿泄露";
            helper.setText(context, true);
            javaMailSender.send(message);
        } else if(ValidationEnum.APPLY.getCode().equals(type)){
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setSubject("【XUNYUYU】申请讲师权限验证");
            helper.setFrom(from);  // 发送人
            helper.setTo(email);
            helper.setSentDate(now);  // 富文本
            String context="<b>尊敬的用户：</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好，您本次申请讲师权限的验证码是："+
                    "<b color=\"'red'\">"  + code + "</b><br>"
                    +"，有效期5分钟。请妥善保管，切勿泄露";
            helper.setText(context, true);
            javaMailSender.send(message);
        }

        // 发送成功之后，把验证码存到数据库
        validationService.saveCode(email, code, type, LocalDateTime.now().plusMinutes(5));
    }

}
