package com.example.springbootgraduation.service;

import com.example.springbootgraduation.entity.dto.UserDTO;
import com.example.springbootgraduation.entity.dto.UserPasswordDTO;
import com.example.springbootgraduation.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.mail.MessagingException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yl
 * @since 2024-04-13
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);


    UserDTO loginEmail(UserDTO userDTO);

    void sendEmailCode(String email, Integer type) throws MessagingException;


}
