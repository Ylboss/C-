package com.example.springbootgraduation.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author yl
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("challenge")
public class Challenge implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 练习表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 练习题目
     */
    private String title;

    /**
     * 题目详情
     */
    private String description;
    /**
     * 星级
     */

    private Integer star;

    /**
     * 题目结果
     */
    private String result;
    /**
     * 题目结果
     */
    @TableField("uploader_name")
    private String uploaderName;
}
