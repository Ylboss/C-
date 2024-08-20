package com.example.springbootgraduation.mapper;

import com.example.springbootgraduation.entity.UserCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yl
 * @since 2024-05-17
 */
@Mapper
public interface UserCourseMapper extends BaseMapper<UserCourse> {

    List<UserCourse> getUserCoursesByUserId(@Param("userId") Integer userId);
}
