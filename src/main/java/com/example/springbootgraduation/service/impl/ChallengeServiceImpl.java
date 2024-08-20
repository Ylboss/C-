package com.example.springbootgraduation.service.impl;

import com.example.springbootgraduation.common.Constants;
import com.example.springbootgraduation.entity.Challenge;
import com.example.springbootgraduation.exception.ServiceException;
import com.example.springbootgraduation.mapper.ChallengeMapper;
import com.example.springbootgraduation.service.IChallengeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yl
 * @since 2024-04-25
 */
@Service
public class ChallengeServiceImpl extends ServiceImpl<ChallengeMapper, Challenge> implements IChallengeService {
    @Resource
    private ChallengeMapper challengeMapper;
    @Transactional
    @Override
    public void setUserChallenge(Integer challengeId, Integer userId) {
        // 验证数据库中是否已存在相同的关联记录
        if (challengeMapper.checkUserCourseExist(challengeId, userId) > 0) {
            // 如果已存在，则可以选择更新操作或者直接返回，这里做更新操作示例
//            courseMapper.updateUserCourse(courseId, studentId);
            throw new ServiceException(Constants.CODE_1000, "你已经成功完成该编程题挑战了！");
        } else {
            // 如果不存在，则执行插入操作

            challengeMapper.setUserChallenge(challengeId, userId);
        }
    }

}
