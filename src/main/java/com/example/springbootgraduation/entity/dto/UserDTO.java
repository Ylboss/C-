package com.example.springbootgraduation.entity.dto;

import com.example.springbootgraduation.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 *接收前端
 */
@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String code;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
    private String role;
    private List<Menu> menus;
}
