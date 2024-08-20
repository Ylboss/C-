package com.example.springbootgraduation.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootgraduation.common.Constants;
import com.example.springbootgraduation.entity.Course;
import com.example.springbootgraduation.exception.ServiceException;
import com.example.springbootgraduation.mapper.CourseMapper;
import com.example.springbootgraduation.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yl
 * @since 2024-05-17
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {
    @Resource
    private CourseMapper courseMapper;

    @Override
    public Page<Course> findPage(Page<Course> page, String name,String teacher) {
        return courseMapper.findPage(page, name, teacher);
    }

    @Transactional
    @Override
    public void setUserCourse(Integer courseId, Integer userId) {
        // 验证数据库中是否已存在相同的关联记录
        if (courseMapper.checkUserCourseExist(courseId, userId) > 0) {
            // 如果已存在，则可以选择更新操作或者直接返回，这里做更新操作示例
//            courseMapper.updateUserCourse(courseId, studentId);
            throw new ServiceException(Constants.CODE_1000, "你已经选择了该课程！");
        } else {
            // 如果不存在，则执行插入操作

            courseMapper.setUserCourse(courseId, userId);
        }
    }

    @Transactional
    @Override
    public void getUserCourse(Integer userId) {
         courseMapper.getUserCourses(userId);
    }
}
