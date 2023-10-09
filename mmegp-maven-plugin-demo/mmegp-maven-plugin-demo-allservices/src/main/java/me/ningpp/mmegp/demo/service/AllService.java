package me.ningpp.mmegp.demo.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import me.ningpp.mmegp.demo.entity.SysMenu;
import me.ningpp.mmegp.demo.entity.SysMenuExample;
import me.ningpp.mmegp.demo.entity.SysRole;
import me.ningpp.mmegp.demo.entity.SysRoleExample;
import me.ningpp.mmegp.demo.entity.SysRoleMenu;
import me.ningpp.mmegp.demo.entity.SysUser;
import me.ningpp.mmegp.demo.model.SysUserRole;

@Transactional
public interface AllService {
    void insertRole(SysRole role);

    void insertMenu(SysMenu menu);

    long insertRoleMenu(SysRoleMenu roleMenu);

    List<SysRole> getRoles(SysRoleExample roleExample);

    List<SysMenu> getMenus(SysMenuExample menuExample);

    List<SysRoleMenu> getRoleMenus(String roleId);

    void deleteRoleMenu(Long roleMenuId);

    void insertUser(SysUser user);

    List<SysUser> queryUser(String nameLike);

    void insertUserRole(SysUserRole userRole);

    List<SysUserRole> queryUserRoles(String userId);

}
