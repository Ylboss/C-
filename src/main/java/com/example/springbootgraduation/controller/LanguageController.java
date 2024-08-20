package com.example.springbootgraduation.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootgraduation.common.Result;
import com.example.springbootgraduation.entity.Language;
import com.example.springbootgraduation.service.ILanguageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-05-15
 */
@RestController
@RequestMapping("/language")
public class LanguageController {

    @Resource
    private ILanguageService languageService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Language language) {
        languageService.saveOrUpdate(language);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        languageService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        languageService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(languageService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(languageService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(required = false) String title) {
        QueryWrapper<Language> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title",title);
        queryWrapper.orderByDesc("id");
        return Result.success(languageService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

