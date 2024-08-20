package com.example.springbootgraduation.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.springbootgraduation.common.Constants;
import com.example.springbootgraduation.common.Result;
import com.example.springbootgraduation.config.AuthAccess;
import com.example.springbootgraduation.entity.dto.UserDTO;
import com.example.springbootgraduation.entity.dto.UserPasswordDTO;
import com.example.springbootgraduation.entity.*;
import com.example.springbootgraduation.exception.ServiceException;
import com.example.springbootgraduation.service.IValidationService;
import org.springframework.web.bind.annotation.*;
import com.example.springbootgraduation.service.IUserService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-04-13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 引入user相关功能
     */
    @Resource
    private IUserService userService;
    @Resource
    private IValidationService validationService;

    /**
     * 数据插入或数据更新，@RequestBody将前台的json对象转换为java对象
     *
     * @param user 用户实体类
     * @return 返回插入或者更新结果
     */
    @PostMapping
    public boolean save(@RequestBody User user) {
        return userService.saveOrUpdate(user);
    }

    /**
     * 删除数据
     *
     * @param id 要删除的用户id
     * @return 返回删除结果
     */
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
    }

    /**
     * 批量删除数据
     *
     * @param ids 要删除的批量用户id
     * @return 返回删除结果
     */
    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return userService.removeByIds(ids);
    }

    /**
     * 测试查询所有信息
     *
     * @return 返回所有用户信息
     */
    @GetMapping
    public List<User> findAll() {
        return userService.list();
    }

    /**
     * 查询当前角色的所有用户
     *
     * @param role 角色
     * @return 返回当前角色的所有用户
     */
    @GetMapping("/role/{role}")
    public Result findUsersByRole(@PathVariable String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 查找数据
     *
     * @param id 要查找的用户id
     * @return 返回查找结果
     */
    @GetMapping("/{id}")
    public User findOne(@PathVariable Integer id) {
        return userService.getById(id);
    }

    /**
     * 分页查询接口
     * 接口路径：/user/page
     *
     * @param pageNum  = (pageNum - 1) * pageSize
     * @param pageSize = pageSize

     * @return 返回分页用户信息
     * @RequestParam 接收?pageNum=1&pageSize=10
     */
    @GetMapping("/page")
    public Result findPage(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address
    ) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.like("email", email);
        }
        if (StringUtils.isNotBlank(address)) {
            queryWrapper.like("address", address);
        }
        // 调用userService的page方法，传入一个Page对象和queryWrapper参数，然后将结果封装成Result.success返回
        return Result.success(userService.page(new Page<>(
                Optional.ofNullable(pageNum).orElse(1), // 如果pageNum为null，则使用默认值1
                Optional.ofNullable(pageSize).orElse(10) // 如果pageSize为null，则使用默认值10
        ), queryWrapper));
    }

    /**
     * 导出接口
     *
     * @param response 导出请求
     * @throws Exception
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<User> list = userService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 自定义标题别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * 导入接口
     *
     * @param file 导入文件
     * @throws Exception
     */
    @PostMapping("/import")
    public Boolean imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setPassword(row.get(1).toString());
            user.setNickname(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            user.setPhone(row.get(4).toString());
            user.setAddress(row.get(5).toString());
            users.add(user);
        }
        userService.saveBatch(users);
        return true;
    }

    /**
     * 用户登录接口
     *
     * @param userDTO 登录的用户信息
     * @return 返回登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "账号或密码未输入！");
        }
        UserDTO dto = userService.login(userDTO);

        return Result.success(dto);
    }


    /**
     * 用户注册接口
     *
     * @param userDTO 登录的用户信息
     * @return 返回登录结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String email = userDTO.getEmail();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password) || StrUtil.isBlank(email)) {
            return Result.error(Constants.CODE_400, "用户名或密码或邮箱出现错误！");
        }

        // 查询数据库中是否已存在相同的邮箱
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);
        User existingUser = userService.getOne(userQueryWrapper);
        if (existingUser != null) {
            return Result.error(Constants.CODE_400, "该邮箱已经存在！请换个邮箱");
        }

        return Result.success(userService.register(userDTO));
    }
    /**
     * 用户个人信息接口
     *
     * @param username 用户名
     * @return 返回用户个人信息
     */
    @GetMapping("/username/{username}")
    public Result findOne(@PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }

    /**
     * 修改密码
     *
     * @param userPasswordDTO 修改密码的结构体
     * @return 返回修改密码的结果
     */
    @PostMapping("/password")
    public Result password(@RequestBody UserPasswordDTO userPasswordDTO) {
        userService.updatePassword(userPasswordDTO);
        return Result.success();
    }



    // 邮箱登录
    @AuthAccess
    @PostMapping("/loginEmail")
    public Result loginEmail(@RequestBody UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();
        if (StrUtil.isBlank(email) || StrUtil.isBlank(code)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.loginEmail(userDTO);
        return Result.success(dto);
    }

    // 忘记密码 | 重置密码
    @AuthAccess
    @PutMapping("/reset")
    public Result reset(@RequestBody UserPasswordDTO userPasswordDTO) {
        if (StrUtil.isBlank(userPasswordDTO.getEmail()) || StrUtil.isBlank(userPasswordDTO.getCode())) {
            throw new ServiceException("-1", "参数异常");
        }
        // 先查询 邮箱验证的表，看看之前有没有发送过  邮箱code，如果不存在，就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", userPasswordDTO.getEmail());
        validationQueryWrapper.eq("code", userPasswordDTO.getCode());
        validationQueryWrapper.ge("time", new Date());  // 查询数据库没过期的code, where time >= new Date()
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码过期，请重新获取");
        }

        // 如果验证通过了，就查询要不通过的信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", userPasswordDTO.getEmail());  //存根据email查询用户信息
        User user = userService.getOne(userQueryWrapper);
        if (user == null) {
            throw new ServiceException("-1", "当前邮箱没有对应账号");
        }
        // 重置密码
        user.setPassword("123");
        userService.updateById(user);
        return Result.success();
    }
    //申请修改为讲师权限
    @AuthAccess
    @PutMapping("/apply")
    public Result apply(@RequestBody UserDTO userDTO) {
        if (StrUtil.isBlank(userDTO.getEmail()) || StrUtil.isBlank(userDTO.getCode())) {
            throw new ServiceException("-1", "参数异常");
        }
        // 先查询 邮箱验证的表，看看之前有没有发送过  邮箱code，如果不存在，就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", userDTO.getEmail());
        validationQueryWrapper.eq("code", userDTO.getCode());
        validationQueryWrapper.ge("time", new Date());  // 查询数据库没过期的code, where time >= new Date()
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码过期，请重新获取");
        }

        // 如果验证通过了，就查询要不通过的信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", userDTO.getEmail());  //存根据email查询用户信息
        User user = userService.getOne(userQueryWrapper);
        if (user == null) {
            throw new ServiceException("-1", "当前邮箱没有对应账号");
        }
        // 重置角色
        user.setRole("ROLE_TEACHER");
        userService.updateById(user);
        return Result.success();
    }
     //发送邮箱验证码
    @AuthAccess
    @GetMapping("/email/{email}/{type}")
    public Result sendEmailCode(@PathVariable String email, @PathVariable Integer type) throws MessagingException {
        if(StrUtil.isBlank(email)) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        if(type == null) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        userService.sendEmailCode(email, type);
        return Result.success();
    }

}



