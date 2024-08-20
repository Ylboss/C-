package com.example.springbootgraduation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author yl
 * @since 2024-05-15
 */
@Getter
@Setter
@TableName("user_challenge")
public class UserChallenge implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private Integer challengeId;

}
