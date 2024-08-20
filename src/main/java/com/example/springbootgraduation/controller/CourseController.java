package com.example.springbootgraduation.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootgraduation.entity.User;
import com.example.springbootgraduation.entity.UserCourse;
import com.example.springbootgraduation.mapper.CourseMapper;
import com.example.springbootgraduation.mapper.UserCourseMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootgraduation.common.Result;

import com.example.springbootgraduation.service.ICourseService;
import com.example.springbootgraduation.entity.Course;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-05-17
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private ICourseService courseService;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private UserCourseMapper userCourseMapper;
    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Course course) {
        courseService.saveOrUpdate(course);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        courseService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        courseService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(courseService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(courseService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name,
                           @RequestParam String teacher) {
        Page<Course> page = courseService.findPage(new Page<>(pageNum, pageSize), name, teacher);
        return Result.success(page);
    }
    //学生选课的多对多消息
    @PostMapping("/userCourse/{courseId}/{userId}")
    public Result UserCourse(@PathVariable Integer courseId, @PathVariable Integer userId) {
        courseService.setUserCourse(courseId, userId);
        return Result.success();
    }
    // 使用@GetMapping注解标识该方法为处理GET请求的方法，请求路径为"/getUserCourseIds/{userId}"
    @GetMapping("/getUserCourseIds/{userId}")
    // 定义方法getUserCourseIds，接收一个Integer类型的userId参数
    public Result getUserCourseIds(@PathVariable Integer userId) {
        // 调用userCourseMapper的selectList方法，查询符合条件"user_id = userId"的UserCourse对象列表
        List<UserCourse> userCourses = userCourseMapper.selectList(new QueryWrapper<UserCourse>().eq("user_id", userId));
        // 使用Stream对userCourses列表进行处理，提取每个UserCourse对象的courseId属性，组成新的List<Integer>
        List<Integer> courseIds = userCourses.stream().map(UserCourse::getCourseId).collect(Collectors.toList());
        // 返回包含courseIds的成功响应Result对象
        return Result.success(courseIds);
    }

}

