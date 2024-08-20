package com.example.springbootgraduation.service.impl;

import com.example.springbootgraduation.entity.Article;
import com.example.springbootgraduation.entity.Challenge;
import com.example.springbootgraduation.mapper.ArticleMapper;
import com.example.springbootgraduation.mapper.ChallengeMapper;
import com.example.springbootgraduation.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yl
 * @since 2024-04-22
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper,Article> implements IArticleService {

}
