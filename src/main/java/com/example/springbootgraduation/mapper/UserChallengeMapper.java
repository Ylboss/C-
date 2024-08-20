package com.example.springbootgraduation.mapper;

import com.example.springbootgraduation.entity.UserChallenge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootgraduation.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yl
 * @since 2024-05-15
 */
@Mapper
public interface UserChallengeMapper extends BaseMapper<UserChallenge> {
    List<UserCourse> getUserChallengeByUserId(@Param("userId") Integer userId);
}
