package com.example.springbootgraduation.mapper;

import com.example.springbootgraduation.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yl
 * @since 2024-04-18
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("select id from role where flag =#{flag}")
    Integer selectByFlag(@Param("flag") String flag);
}
