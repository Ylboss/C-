package com.example.springbootgraduation.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootgraduation.entity.UserChallenge;
import com.example.springbootgraduation.entity.UserCourse;
import com.example.springbootgraduation.mapper.UserChallengeMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootgraduation.common.Result;

import com.example.springbootgraduation.service.IChallengeService;
import com.example.springbootgraduation.entity.Challenge;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-04-25
 */
@RestController
@RequestMapping("/challenge")
public class ChallengeController {

    @Resource
    private IChallengeService challengeService;
    @Resource
    private UserChallengeMapper userChallengeMapper;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Challenge challenge) {
        challengeService.saveOrUpdate(challenge);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        challengeService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        challengeService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 根据条件进行模糊查询列表
     * @param uploaderName 查询条件
     * @param title 查询条件
     * @param star 查询条件
     * @return 查询结果列表
     */

    @GetMapping("/search")
    public Result search(@RequestParam(defaultValue = "") String uploaderName,
                         @RequestParam(defaultValue = "") String title,
                         @RequestParam(defaultValue = "0") Integer star) {
        // 创建QueryWrapper对象queryWrapper
        QueryWrapper<Challenge> queryWrapper = new QueryWrapper<>();
        int conditionCount = 0; // 记录满足条件的数量
        if (!StringUtils.isEmpty(uploaderName)) {
            queryWrapper.like("uploader_name", uploaderName);
            conditionCount++;
        }
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.or(i -> i.like("title", title));
            conditionCount++;
        }
        if (star != 0) {
            queryWrapper.or(i -> i.eq("star", star));
            conditionCount++;
        }
        // 如果满足条件的数量大于1，则使用and连接条件
        if (conditionCount > 1) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i.like("uploader_name", uploaderName)
                    .like("title", title)
                    .eq("star", star));
        }
        // 调用challengeService的list方法，传入queryWrapper作为参数，并将结果封装成Result.success返回
        return Result.success(challengeService.list(queryWrapper));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(challengeService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(challengeService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String title
                          ) {
        QueryWrapper<Challenge> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", title);
        queryWrapper.orderByDesc("id");
        return Result.success(challengeService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
    //记录编程题挑战成功的用户
    @PostMapping("/userChallenge/{challengeId}/{userId}")
    // 使用@PostMapping注解标识该方法为处理POST请求的方法，请求路径为"/userChallenge/{challengeId}/{userId}"
    public Result UserChallenge(@PathVariable Integer challengeId, @PathVariable Integer userId) {
        // 调用challengeService的setUserChallenge方法，设置用户挑战信息
        challengeService.setUserChallenge(challengeId, userId);
        // 返回成功响应Result对象
        return Result.success();
    }

    @GetMapping("/getUserChallengeIds/{userId}")
    // 使用@GetMapping注解标识该方法为处理GET请求的方法，请求路径为"/getUserChallengeIds/{userId}"
    // 定义方法getUserChallengeIds，接收一个Integer类型的userId参数
    public Result getUserChallengeIds(@PathVariable Integer userId) {
        // 调用userChallengeMapper的selectList方法，查询符合条件"user_id = userId"的UserChallenge对象列表
        List<UserChallenge> userChallenges = userChallengeMapper.selectList(new QueryWrapper<UserChallenge>().eq("user_id", userId));
        // 使用Stream对userChallenges列表进行处理，提取每个UserChallenge对象的challengeId属性，组成新的List<Integer>
        List<Integer> challengeIds = userChallenges.stream().map(UserChallenge::getChallengeId).collect(Collectors.toList());
        // 返回包含challengeIds的成功响应Result对象
        return Result.success(challengeIds);
    }
}

