package com.example.springbootgraduation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@TableName("role_menu")
public class RoleMenu {
    private Integer roleId;
    private Integer menuId;
}
