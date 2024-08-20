package com.example.springbootgraduation.entity;

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
 * @since 2024-05-17
 */
@Getter
@Setter
@TableName("user_course")
public class UserCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学生id
     */
    private Integer userId;

    /**
     * 课程id
     */
    private Integer courseId;


}
