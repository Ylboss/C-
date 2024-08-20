package com.example.springbootgraduation.service;

import cn.hutool.core.date.DateTime;
import com.example.springbootgraduation.entity.Validation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yl
 * @since 2024-05-09
 */
public interface IValidationService extends IService<Validation> {

    @Transactional
    void saveCode(String email, String code, Integer type, LocalDateTime expireDate);
}
