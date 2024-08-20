package com.example.springbootgraduation.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootgraduation.common.Result;

import com.example.springbootgraduation.service.IUserChallengeService;
import com.example.springbootgraduation.entity.UserChallenge;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-05-15
 */
@RestController
@RequestMapping("/user-challenge")
public class UserChallengeController {

    @Resource
    private IUserChallengeService userChallengeService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody UserChallenge userChallenge) {
        userChallengeService.saveOrUpdate(userChallenge);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userChallengeService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        userChallengeService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userChallengeService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userChallengeService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<UserChallenge> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(userChallengeService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

