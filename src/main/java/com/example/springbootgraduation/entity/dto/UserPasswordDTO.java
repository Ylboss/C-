package com.example.springbootgraduation.entity.dto;

import lombok.Data;

@Data
public class UserPasswordDTO {
    private String username;
    private String phone;
    private String password;
    private String newPassword;
    private String email;
    private String code;
}
