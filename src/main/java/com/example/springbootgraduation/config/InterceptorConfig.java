package com.example.springbootgraduation.config;

import com.example.springbootgraduation.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    // 重写addInterceptors方法，用于添加拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加JwtInterceptor拦截器到拦截器注册表
        registry.addInterceptor(jwtInterceptor()).addPathPatterns("/**").
                excludePathPatterns("/user/login", "/user/register", "/**/export", "/**/import", "/**/file/**", "/api/**", "/comment/**", "/challenge/**", "/imserver/**");
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }


}
