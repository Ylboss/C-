package com.example.springbootgraduation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootgraduation.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yl
 * @since 2024-05-17
 */
public interface ICourseService extends IService<Course> {
    Page<Course> findPage(Page<Course> page, String name,String teacher);

    void setUserCourse(Integer courseId, Integer userId);

    @Transactional
    void getUserCourse(Integer userId);
}
