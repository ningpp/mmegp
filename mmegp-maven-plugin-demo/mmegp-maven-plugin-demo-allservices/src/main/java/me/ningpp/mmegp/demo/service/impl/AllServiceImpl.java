package me.ningpp.mmegp.demo.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import me.ningpp.mmegp.demo.entity.SysAutoUser;
import me.ningpp.mmegp.demo.mapper.SysAutoUserMapper;
import me.ningpp.mmegp.mybatis.dsql.pagination.LimitOffset;
import me.ningpp.mmegp.mybatis.dsql.pagination.Page;
import me.ningpp.mmegp.mybatis.dsql.pagination.PaginationModelRenderer;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.where.condition.IsLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.ningpp.mmegp.demo.dao.SysUserRoleMapper;
import me.ningpp.mmegp.demo.entity.SysMenu;
import me.ningpp.mmegp.demo.entity.SysMenuExample;
import me.ningpp.mmegp.demo.entity.SysRole;
import me.ningpp.mmegp.demo.entity.SysRoleExample;
import me.ningpp.mmegp.demo.entity.SysRoleMenu;
import me.ningpp.mmegp.demo.entity.SysRoleMenuExample;
import me.ningpp.mmegp.demo.entity2.SysUser;
import me.ningpp.mmegp.demo.mapper.SysMenuMapper;
import me.ningpp.mmegp.demo.mapper.SysRoleMapper;
import me.ningpp.mmegp.demo.mapper.SysRoleMenuMapper;
import me.ningpp.mmegp.demo.mapper.SysUserDynamicSqlSupport;
import me.ningpp.mmegp.demo.mapper.SysUserMapper;
import me.ningpp.mmegp.demo.model.SysUserRole;
import me.ningpp.mmegp.demo.model.SysUserRoleExample;
import me.ningpp.mmegp.demo.service.AllService;

@Service
public class AllServiceImpl implements AllService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysUserMapper sysUserMapper;
    private final SysAutoUserMapper sysAutoUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PaginationModelRenderer renderer;

    @Autowired
    public AllServiceImpl(PaginationModelRenderer renderer,
                          SysRoleMapper sysRoleMapper,
                          SysRoleMenuMapper sysRoleMenuMapper,
                          SysMenuMapper sysMenuMapper,
                          SysUserMapper sysUserMapper,
                          SysAutoUserMapper sysAutoUserMapper,
                          SysUserRoleMapper sysUserRoleMapper) {
        this.renderer = renderer;
        this.sysRoleMapper = sysRoleMapper;
        this.sysRoleMenuMapper = sysRoleMenuMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysAutoUserMapper = sysAutoUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public void insertRole(SysRole role) {
        sysRoleMapper.insert(role);
    }

    @Override
    public void insertMenu(SysMenu menu) {
        sysMenuMapper.insert(menu);
    }

    @Override
    public int deleteMenu(SysMenuExample menuExample) {
        return sysMenuMapper.deleteByExample(menuExample);
    }

    @Override
    public long insertRoleMenu(SysRoleMenu roleMenu) {
        sysRoleMenuMapper.insert(roleMenu);
        return roleMenu.getId();
    }

    @Override
    public List<SysRole> getRoles(SysRoleExample roleExample) {
        return sysRoleMapper.selectByExample(roleExample);
    }

    @Override
    public List<SysMenu> getMenus(SysMenuExample menuExample) {
        return sysMenuMapper.selectByExample(menuExample);
    }

    @Override
    public List<SysRoleMenu> getRoleMenus(String roleId) {
        SysRoleMenuExample roleMenuExample = new SysRoleMenuExample();
        roleMenuExample.createCriteria().andRoleIdEqualTo(roleId);
        return sysRoleMenuMapper.selectByExample(roleMenuExample);
    }

    @Override
    public void deleteRoleMenu(Long roleMenuId) {
        sysRoleMenuMapper.deleteByPrimaryKey(roleMenuId);
    }

    @Override
    public void insertAutoUser(SysAutoUser autoUser) {
        sysAutoUserMapper.insert(autoUser);
    }

    @Override
    public void batchInsertAutoUser(Collection<SysAutoUser> autoUsers) {
        sysAutoUserMapper.insertMultiple(autoUsers);
    }

    @Override
    public List<SysAutoUser> queryAutoUser(SelectDSLCompleter completer) {
        return sysAutoUserMapper.select(completer);
    }

    @Override
    public Page<SysAutoUser> selectAutoUserPage(SelectDSL<SelectModel> dsl, LimitOffset limitOffset) {
        return sysAutoUserMapper.selectPage(dsl, limitOffset);
    }

    @Override
    public void insertUser(SysUser user) {
        sysUserMapper.insert(user);
    }

    @Override
    public List<SysUser> queryUser(String nameLike) {
        return sysUserMapper.selectMany(SqlBuilder.select(SysUserMapper.selectList)
                .from(SysUserDynamicSqlSupport.sysUser)
                .where().and(SysUserDynamicSqlSupport.name, IsLike.of(String.format(Locale.ENGLISH, "%%%s%%", nameLike)))
                .build().render(RenderingStrategies.MYBATIS3));
    }

    @Override
    public Page<SysUser> selectUserPage(SelectDSL<SelectModel> dsl, LimitOffset limitOffset) {
        return sysUserMapper.selectPage(dsl, limitOffset, renderer);
    }

    @Override
    public void insertUserRole(SysUserRole userRole) {
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public List<SysUserRole> queryUserRoles(String userId) {
        SysUserRoleExample example = new SysUserRoleExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return sysUserRoleMapper.selectByExample(example);
    }

}
