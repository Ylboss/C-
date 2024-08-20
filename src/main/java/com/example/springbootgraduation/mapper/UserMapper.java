package com.example.springbootgraduation.mapper;

import com.example.springbootgraduation.entity.dto.UserPasswordDTO;
import com.example.springbootgraduation.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yl
 * @since 2024-04-13
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    int updatePassword(@Param("userPasswordDTO") UserPasswordDTO userPasswordDTO);

}
