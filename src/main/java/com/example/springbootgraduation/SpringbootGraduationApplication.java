package com.example.springbootgraduation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.springbootgraduation.mapper")

public class SpringbootGraduationApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringbootGraduationApplication.class, args);
    }


}
