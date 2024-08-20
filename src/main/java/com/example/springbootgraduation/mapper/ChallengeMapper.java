package com.example.springbootgraduation.mapper;

import com.example.springbootgraduation.entity.Challenge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yl
 * @since 2024-04-25
 */
@Mapper
public interface ChallengeMapper extends BaseMapper<Challenge> {
    void setUserChallenge(@Param("challengeId") Integer challengeId, @Param("userId") Integer studentId);
    int checkUserCourseExist(@Param("challengeId") Integer challengeId, @Param("userId") Integer userId);
}
