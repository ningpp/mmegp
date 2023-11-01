package me.ningpp.mmegp.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import me.ningpp.mmegp.demo.DemoApplicationStarter;
import me.ningpp.mmegp.demo.entity.SysMenu;
import me.ningpp.mmegp.demo.entity.SysMenuExample;
import me.ningpp.mmegp.demo.entity.SysRole;
import me.ningpp.mmegp.demo.entity.SysRoleExample;
import me.ningpp.mmegp.demo.entity.SysRoleMenu;
import me.ningpp.mmegp.demo.entity.SysUser;
import me.ningpp.mmegp.demo.model.SysUserRole;

public class AllServiceTest extends DemoApplicationStarter {

    @Test
    void typeHandlerTest() {
        SysMenu menu1 = new SysMenu();
        menu1.setId(uuid());
        menu1.setName(uuid());
        menu1.setParentId(uuid());
        UUID uuid = UUID.randomUUID();
        menu1.setUuid(uuid);
        menu1.setNoDashUUID(uuid);
        menu1.setWithDashUUID(uuid);
        allService.insertMenu(menu1);

        SysMenuExample example = new SysMenuExample();
        example.createCriteria().andUuidEqualTo(uuid);
        List<SysMenu> menus = allService.getMenus(example);
        assertTrue(menus.size() == 1 && menu1.getId().equals(menus.get(0).getId()));

        example = new SysMenuExample();
        example.createCriteria().andNoDashUUIDEqualTo(uuid);
        menus = allService.getMenus(example);
        assertTrue(menus.size() == 1 && menu1.getId().equals(menus.get(0).getId()));

        example = new SysMenuExample();
        example.createCriteria().andWithDashUUIDEqualTo(uuid);
        menus = allService.getMenus(example);
        assertTrue(menus.size() == 1 && menu1.getId().equals(menus.get(0).getId()));

        assertEquals(1, allService.deleteMenu(example));
    }

    @Test
    void userRoleTest() {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(uuid());
        userRole.setRoleId(uuid());
        allService.insertUserRole(userRole);
        assertTrue(userRole.getId() > 0);

        List<SysUserRole> userRoles = allService.queryUserRoles(userRole.getUserId());
        assertEquals(1, userRoles.size());
        assertEquals(userRole.getId(), userRoles.get(0).getId());
        assertEquals(userRole.getRoleId(), userRoles.get(0).getRoleId());
    }

    @Test
    void userTest() {
        SysUser user = new SysUser();
        user.setId(uuid());
        user.setName("javascript");
        allService.insertUser(user);

        List<SysUser> users = allService.queryUser("java");
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
        users = allService.queryUser("typescript");
        assertEquals(0, users.size());
    }

    @Test
    void allTest() {
        assertTrue(allService.getRoleMenus(uuid()).isEmpty());

        SysRole role1 = new SysRole();
        role1.setId(uuid());
        role1.setName("role1");
        allService.insertRole(role1);

        SysRole role2 = new SysRole();
        role2.setId(uuid());
        role2.setName("role2");
        allService.insertRole(role2);

        SysRole role3 = new SysRole();
        role3.setId(uuid());
        role3.setName("role3");
        allService.insertRole(role3);

        SysMenu menu1 = new SysMenu();
        menu1.setId(uuid());
        menu1.setName("menu1");
        menu1.setParentId("");
        allService.insertMenu(menu1);

        SysMenu menu2 = new SysMenu();
        menu2.setId(uuid());
        menu2.setName("menu2");
        menu2.setParentId("");
        allService.insertMenu(menu2);

        saveRoleMenus(role1, Arrays.asList(menu1));
        saveRoleMenus(role2, Arrays.asList(menu1, menu2));
        saveRoleMenus(role3, Collections.emptyList());

        assertEquals(1, allService.getRoleMenus(role1.getId()).size());
        assertEquals(2, allService.getRoleMenus(role2.getId()).size());
        assertEquals(0, allService.getRoleMenus(role3.getId()).size());

        SysRoleExample roleExample = new SysRoleExample();
        roleExample.createCriteria().andIdEqualTo(role3.getId()).andNameEqualTo(role3.getName());
        assertEquals(1, allService.getRoles(roleExample).size());

        roleExample = new SysRoleExample();
        roleExample.createCriteria().andIdEqualTo(role3.getId()).andNameEqualTo(uuid());
        assertEquals(0, allService.getRoles(roleExample).size());

        SysMenuExample menuExample = new SysMenuExample();
        menuExample.createCriteria().andParentIdEqualTo("").andNameLike("m%");
        assertEquals(2, allService.getMenus(menuExample).size());
    }

    void saveRoleMenus(SysRole role, List<SysMenu> menus) {
        allService.getRoleMenus(role.getId()).forEach(roleMenu -> allService.deleteRoleMenu(roleMenu.getId()));

        for (SysMenu sysMenu : menus) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(role.getId());
            roleMenu.setMenuId(sysMenu.getId());
            allService.insertRoleMenu(roleMenu);
        }
    }
}
