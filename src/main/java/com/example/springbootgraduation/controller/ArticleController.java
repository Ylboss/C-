package com.example.springbootgraduation.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

import com.example.springbootgraduation.common.Result;
import com.example.springbootgraduation.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import com.example.springbootgraduation.service.IArticleService;
import com.example.springbootgraduation.entity.Article;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-04-22
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Article article) {
        // 新增
        if (article.getId() == null) {
            article.setTime(DateUtil.now()); // new Date()
            article.setUser(TokenUtils.getCurrentUser().getNickname());
            article.setUserId(TokenUtils.getCurrentUser().getId());
        }
        articleService.saveOrUpdate(article);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        articleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        articleService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(articleService.list());
    }

    // 使用@GetMapping注解表示处理HTTP GET请求的方法
    // @PathVariable注解用于将URL中的模板变量绑定到方法参数上
    // Result.success()方法返回一个成功的结果对象
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        // 调用articleService的getById方法获取指定id的文章信息，并返回成功的结果对象
        return Result.success(articleService.getById(id));
    }

    @GetMapping("/page")
    // 使用@GetMapping注解标识该方法为处理GET请求的方法，映射路径为"/page"
    public Result findPage(@RequestParam String name, // 从请求参数中获取name
                           @RequestParam Integer pageNum, // 从请求参数中获取pageNum
                           @RequestParam Integer pageSize) { // 从请求参数中获取pageSize
        // 创建查询条件包装器
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        return Result.success(articleService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }


}

