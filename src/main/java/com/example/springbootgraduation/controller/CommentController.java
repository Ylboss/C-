package com.example.springbootgraduation.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.springbootgraduation.common.Result;
import com.example.springbootgraduation.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.example.springbootgraduation.service.ICommentService;
import com.example.springbootgraduation.entity.Comment;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yl
 * @since 2024-04-22
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static final String COMMENT_KEY = "COMMENT";
    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Comment comment) {
        if (comment.getId() == null) { // 新增评论
            comment.setUserId(TokenUtils.getCurrentUser().getId());
            comment.setTime(DateUtil.now());
            if (comment.getPid() != null) {  // 判断如果是回复，进行处理
                Integer pid = comment.getPid();
                Comment pComment = commentService.getById(pid);
                if (pComment.getOriginId() != null) {  // 如果当前回复的父级有祖宗，那么就设置相同的祖宗
                    comment.setOriginId(pComment.getOriginId());
                } else {  // 否则就设置父级为当前回复的祖宗
                    comment.setOriginId(comment.getPid());
                }
            }
        }
        commentService.saveOrUpdate(comment);
        flushRedis(COMMENT_KEY);
        return Result.success();

    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        flushRedis(COMMENT_KEY);
        commentService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        commentService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(commentService.list());
    }

    @GetMapping("/tree/{articleId}")
    public Result findTree(@PathVariable Integer articleId) {

//        String jsonStr = stringRedisTemplate.opsForValue().get(COMMENT_KEY); // 从Redis中获取JSON字符串
//        List<Comment> articleComments;
//        if (jsonStr != null) {
//            if (jsonStr != null) {
//                articleComments = JSONUtil.toList(JSONUtil.parseArray(jsonStr), Comment.class); // 将JSON字符串转换为评论对象列表
//            } else {
//                articleComments = null;
//            }
//        } else {
//            articleComments = commentService.findCommentDetail(articleId); // 从数据库中获取评论数据
////            // 将评论数据存入Redis缓存
////            stringRedisTemplate.opsForValue().set(COMMENT_KEY, JSONUtil.toJsonStr(articleComments));
//        }
        List<Comment> articleComments = commentService.findCommentDetail(articleId);
        List<Comment> originList = articleComments.stream().filter(comment -> comment.getOriginId() == null && comment.getArticleId().equals(articleId)).collect(Collectors.toList());
        // 设置评论数据的子节点，也就是回复内容
        for (Comment origin : originList) {
            List<Comment> comments = articleComments.stream().filter(comment -> origin.getId().equals(comment.getOriginId())).collect(Collectors.toList());  // 表示回复对象集合
            comments.forEach(comment -> {
                Optional<Comment> pComment = articleComments.stream().filter(c1 -> c1.getId().equals(comment.getPid())).findFirst();  // 找到当前评论的父级
                pComment.ifPresent((v -> {  // 找到父级评论的用户id和用户昵称，并设置给当前的回复对象
                    comment.setPUserId(v.getUserId());
                    comment.setPNickname(v.getNickname());
                }));
            });
            origin.setChildren(comments);
        }
        flushRedis(COMMENT_KEY);
        return Result.success(originList);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(commentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 删除缓存
     *
     * @param key 要删除的缓存key
     */
    private void flushRedis(String key) {
        stringRedisTemplate.delete(key);
    }
}
