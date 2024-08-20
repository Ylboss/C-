package com.example.springbootgraduation.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootgraduation.entity.Menu;
import com.example.springbootgraduation.entity.Role;
import com.example.springbootgraduation.entity.RoleMenu;
import com.example.springbootgraduation.mapper.RoleMapper;
import com.example.springbootgraduation.mapper.RoleMenuMapper;
import com.example.springbootgraduation.service.IMenuService;
import com.example.springbootgraduation.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yl
 * @since 2024-04-18
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    RoleMenuMapper roleMenuMapper;
    // 重写接口中的方法，设置角色菜单关联关系
    @Resource
    private IMenuService menuService;
    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds){
        // 创建查询条件构造器
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        // 添加查询条件：角色ID等于传入的roleId
        queryWrapper.eq("role_id", roleId);
        // 根据查询条件删除角色菜单关联关系
        roleMenuMapper.delete(queryWrapper);

        List<Integer> menuIdsCopy = CollUtil.newArrayList(menuIds);
        for (Integer menuId : menuIds) {
            Menu menu = menuService.getById(menuId);
            if (menu.getPid() != null && !menuIdsCopy.contains(menu.getPid())) { // 二级菜单 并且传过来的menuId数组里面没有它的父级id
                // 那么我们就得补上这个父级id
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menu.getPid());
                roleMenuMapper.insert(roleMenu);
                menuIdsCopy.add(menu.getPid());
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }
    public List<Integer> getRoleMenu(Integer roleId){
        return roleMenuMapper.selectByRoleId(roleId);
    }
}
