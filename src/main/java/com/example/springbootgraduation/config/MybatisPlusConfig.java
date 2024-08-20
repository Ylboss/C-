package com.example.springbootgraduation.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@MapperScan 是 MyBatis Plus 提供的注解，它的作用是扫描指定包下的所有接口，
// 将其注册成 MyBatis 的 Mapper。在 MyBatis Plus 中，
// 它是用于替代原生 MyBatis 中 XML 配置文件中的 <mapper> 标签的一种方式，
// 可以自动扫描指定包下的接口，无需手动在 XML 中配置。

@MapperScan("com.example.springbootgraduation.mapper")
public class MybatisPlusConfig {
//    最新版
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
