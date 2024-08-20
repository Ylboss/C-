package com.example.springbootgraduation.mapper;

import com.example.springbootgraduation.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yl
 * @since 2024-04-22
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
