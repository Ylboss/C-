package com.example.springbootgraduation.service;

import com.example.springbootgraduation.entity.Challenge;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yl
 * @since 2024-04-25
 */
public interface IChallengeService extends IService<Challenge> {

    void setUserChallenge(Integer challengeId, Integer userId);


}
