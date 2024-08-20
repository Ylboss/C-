package com.example.springbootgraduation.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springbootgraduation.entity.User;
import com.example.springbootgraduation.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {
    // 声明私有静态变量staticUserService，用于存储UserService实例
    @Autowired
    private static IUserService staticUserService;

    // 使用@Autowired注解将UserService实例注入到userService变量中
    @Autowired
    private IUserService userService;

    /**
     * 生成Token的工具类
     */
    public static String genToken(String userId, String sign) {
        // 使用JWT创建Token，并设置Token的受众为userId，过期时间为当前时间后3小时
        return JWT.create().withAudience(userId)
                .withExpiresAt(DateUtil.offsetHour(new Date(), 3))
                .sign(Algorithm.HMAC256(sign));
    }

    public static User getCurrentUser() {
        // 获取当前请求
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            // 从请求头中获取token
            String token = request.getHeader("token");
            // 如果token不为空
            if (StrUtil.isNotBlank(token)) {
                // 解析token获取用户ID
                String userId = JWT.decode(token).getAudience().get(0);
                // 根据用户ID从数据库中获取用户信息
                return staticUserService.getById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            // 捕获异常并打印异常信息
            e.printStackTrace();
        }
        // 若出现异常或token为空，则返回null
        return null;
    }

    // 在Bean初始化后执行的方法，将userService赋值给staticUserService
    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }
}
