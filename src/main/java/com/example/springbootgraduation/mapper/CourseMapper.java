package com.example.springbootgraduation.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootgraduation.entity.Course;
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
public interface CourseMapper extends BaseMapper<Course> {

    Page<Course> findPage(Page<Course> page, @Param("name") String name,@Param("teacher") String teacher);

    void deleteUserCourse(@Param("courseId") Integer courseId, @Param("userId") Integer studentId);

    void setUserCourse(@Param("courseId") Integer courseId, @Param("userId") Integer studentId);

    int checkUserCourseExist(@Param("courseId")Integer courseId, @Param("userId") Integer userId);

    void updateUserCourse(@Param("courseId")Integer courseId, @Param("userId")Integer userId);

    List<Course> getUserCourses(@Param("userId") Integer userId);
}
