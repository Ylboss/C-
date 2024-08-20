package com.example.springbootgraduation.exception;

import com.example.springbootgraduation.common.Result;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 控制器通知注解，用于定义全局异常处理类
@ControllerAdvice
public class GlobalExceptionHandler {
    // 声明异常处理方法，处理ServiceException异常
    @ExceptionHandler(ServiceException.class)
    // 将方法返回的结果转换为JSON格式
    @ResponseBody
    public Result handle(ServiceException se){
        // 调用Result类的error方法，返回包含异常信息的Result对象
        return Result.error(se.getCode(),se.getMessage());
    }
}