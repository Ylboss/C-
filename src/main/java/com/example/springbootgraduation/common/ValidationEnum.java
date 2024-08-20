package com.example.springbootgraduation.common;

public enum ValidationEnum {
    LOGIN(1),FORGET_PASS(2),APPLY(3);
    private Integer code;
    public Integer getCode(){
        return code;
    }
    ValidationEnum(Integer code) {
        this.code = code;
    }
}
