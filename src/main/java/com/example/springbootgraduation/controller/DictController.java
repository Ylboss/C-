package com.example.springbootgraduation.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.example.springbootgraduation.service.IDictService;
import com.example.springbootgraduation.entity.Dict;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-04-18
 */
@RestController
@RequestMapping("/dict")
public class DictController {
    @Autowired
    private IDictService dictService;

    //查询Dict表所有信息
    @GetMapping("/findAllDict")
    public List<Dict> findAllDict() {
        //调用service的方法
        return dictService.list();
    }

    //根据id获取Dict
    @GetMapping("/getDictById/{id}")
    public Dict getDictById(@PathVariable Integer id) {
        return dictService.getById(id);
    }

    //条件查询带分页
    @GetMapping("/findPageDict")
    public Page<Dict> findPageDict(@RequestParam Integer pageNum,
                                   @RequestParam Integer pageSize) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        return dictService.page(new Page<>(pageNum, pageSize));
    }


    //添加Dict
    //修改Dict信息
    @PostMapping("/saveDict")
    public boolean saveDict(@RequestBody Dict dict) {
        //调用service
        return dictService.saveOrUpdate(dict);
    }


    //删除Dict
    @DeleteMapping("/{id}")
    public boolean removeDict(@PathVariable Integer id) {
        return dictService.removeById(id);
    }

    //批量删除Dict
    @PostMapping("/delete/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return dictService.removeByIds(ids);
    }


}

