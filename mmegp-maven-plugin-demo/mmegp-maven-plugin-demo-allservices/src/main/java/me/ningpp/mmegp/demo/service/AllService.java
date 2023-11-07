package me.ningpp.mmegp.demo.service;

import java.util.Collection;
import java.util.List;

import me.ningpp.mmegp.demo.entity.SysAutoUser;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import org.mybatis.dynamic.sql.select.PagingModel;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.transaction.annotation.Transactional;

import me.ningpp.mmegp.demo.entity.SysMenu;
import me.ningpp.mmegp.demo.entity.SysMenuExample;
import me.ningpp.mmegp.demo.entity.SysRole;
import me.ningpp.mmegp.demo.entity.SysRoleExample;
import me.ningpp.mmegp.demo.entity.SysRoleMenu;
import me.ningpp.mmegp.demo.entity2.SysUser;
import me.ningpp.mmegp.demo.model.SysUserRole;

@Transactional
public interface AllService {
    void insertRole(SysRole role);

    void insertMenu(SysMenu menu);

    int deleteMenu(SysMenuExample menuExample);

    long insertRoleMenu(SysRoleMenu roleMenu);

    List<SysRole> getRoles(SysRoleExample roleExample);

    List<SysMenu> getMenus(SysMenuExample menuExample);

    List<SysRoleMenu> getRoleMenus(String roleId);

    void deleteRoleMenu(Long roleMenuId);

    void insertAutoUser(SysAutoUser autoUser);

    void batchInsertAutoUser(Collection<SysAutoUser> autoUsers);

    List<SysAutoUser> queryAutoUser(SelectDSLCompleter completer);

    Page<SysAutoUser> selectAutoUserPage(SelectDSL<SelectModel> dsl, PagingModel pagingModel);

    void insertUser(SysUser user);

    List<SysUser> queryUser(String nameLike);

    Page<SysUser> selectUserPage(SelectDSL<SelectModel> dsl, PagingModel pagingModel);

    void insertUserRole(SysUserRole userRole);

    List<SysUserRole> queryUserRoles(String userId);

}
