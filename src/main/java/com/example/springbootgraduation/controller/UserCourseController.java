package com.example.springbootgraduation.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootgraduation.common.Result;

import com.example.springbootgraduation.service.IUserCourseService;
import com.example.springbootgraduation.entity.UserCourse;

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
@RequestMapping("/usercourse")
public class UserCourseController {

    @Resource
    private IUserCourseService userCourseService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody UserCourse userCourse) {
        userCourseService.saveOrUpdate(userCourse);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userCourseService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        userCourseService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userCourseService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userCourseService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<UserCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(userCourseService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

