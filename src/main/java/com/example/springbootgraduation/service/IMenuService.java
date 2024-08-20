package com.example.springbootgraduation.service;

import com.example.springbootgraduation.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yl
 * @since 2024-04-18
 */
public interface IMenuService extends IService<Menu> {
    List<Menu> findMenus(String name);

}
