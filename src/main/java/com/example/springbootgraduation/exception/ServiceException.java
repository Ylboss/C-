package com.example.springbootgraduation.exception;

import lombok.Getter;

/**
 * z自定义异常
 */
@Getter
public class ServiceException extends RuntimeException{
    private String code;
    public ServiceException(String code,String msg){
        super(msg);
        this.code = code;

    }
}
