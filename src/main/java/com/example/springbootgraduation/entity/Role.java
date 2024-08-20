package com.example.springbootgraduation.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author yl
 * @since 2024-04-18
 */
@Getter
@Setter
@TableName("role")
@ToString
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type= IdType.AUTO)
    /**
     * id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


    private String flag;
}
