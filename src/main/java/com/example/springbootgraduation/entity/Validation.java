package com.example.springbootgraduation.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author yl
 * @since 2024-05-09
 */
@Getter
@Setter
public class Validation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证类型
     */
    private Integer type;

    /**
     * 过期时间
     */
    private LocalDateTime time;


}
