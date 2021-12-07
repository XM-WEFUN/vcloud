package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.admin.dto.MenuItem;
import com.bootvue.admin.service.MenuMapperService;
import com.bootvue.admin.service.MenuService;
import com.bootvue.admin.service.RoleMapperService;
import com.bootvue.admin.service.RoleMenuMapperService;
import com.bootvue.admin.util.MenuUtil;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.model.AppUser;
import com.bootvue.datasource.entity.Menu;
import com.bootvue.datasource.entity.Role;
import com.bootvue.datasource.entity.RoleMenu;
import com.bootvue.datasource.type.MenuTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final MenuMapperService menuMapperService;
    private final RoleMenuMapperService roleMenuMapperService;
    private final RoleMapperService roleMapperService;

    @Override
    public List<MenuItem> list(Integer type, AppUser user) {
        Set<MenuItem> menus;
        if (AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            menus = menuMapperService.menus();
        } else {
            // 此租户  超级管理员拥有的菜单
            menus = roleMenuMapperService.menus(user.getTenantId());
        }

        return MenuUtil.handleMenus(menus, type);
    }

    @Override
    public void delete(Long id) {
        Set<Long> ids = new HashSet<>();

        // 删除menu role_menu
        Menu menu = menuMapperService.getById(id);
        Assert.notNull(menu, "参数错误");
        ids.add(id);

        // 删除子菜单
        List<Menu> menus = menuMapperService.list(new QueryWrapper<>(new Menu().setPId(id)));
        ids.addAll(menus.stream().map(Menu::getId).collect(Collectors.toSet()));

        log.info("删除菜单: {}", ids);

        menuMapperService.removeByIds(ids);

        roleMenuMapperService.remove(new QueryWrapper<RoleMenu>().lambda().in(RoleMenu::getMenuId, ids));
    }

    @Override
    public void addOrUpdate(MenuItem param) {
        Menu menu;
        if (!ObjectUtils.isEmpty(param.getId())) {
            // 更新
            menu = menuMapperService.getById(param.getId());
        } else {
            // 新增
            Assert.hasText(param.getTitle(), "参数错误");
            menu = new Menu();
            menu.setPId(ObjectUtils.isEmpty(param.getPid()) ? 0L : param.getPid());
        }
        menu.setTitle(param.getTitle());
        menu.setKey(StringUtils.hasText(param.getKey()) ? param.getKey() : "");
        menu.setPath(StringUtils.hasText(param.getPath()) ? param.getPath() : "");
        menu.setIcon(StringUtils.hasText(param.getIcon()) ? param.getIcon() : "");
        menu.setAction(StringUtils.hasText(param.getAction()) ? param.getAction() : "");
        menu.setSort(ObjectUtils.isEmpty(param.getSort()) ? 0 : param.getSort());
        menu.setType(MenuTypeEnum.find(param.getType()));
        if (menu.getType().equals(MenuTypeEnum.MENU)) {
            menu.setShow(!ObjectUtils.isEmpty(param.getShow()) && param.getShow());
            menu.setDefaultSelect(!ObjectUtils.isEmpty(param.getDefaultSelect()) && param.getDefaultSelect());
            menu.setDefaultOpen(!ObjectUtils.isEmpty(param.getDefaultOpen()) && param.getDefaultOpen());
        } else {
            menu.setShow(false);
            menu.setDefaultSelect(false);
            menu.setDefaultOpen(false);
        }

        menuMapperService.saveOrUpdate(menu);
    }

    @Override
    public Set<String> listByRole(Long roleId, AppUser user) {
        // 此角色是否属于此租户
        Role role = roleMapperService.getById(roleId);
        Assert.notNull(role, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Assert.isTrue(role.getTenantId().equals(user.getTenantId()), "参数错误");
        }

        List<RoleMenu> menus = roleMenuMapperService.list(new QueryWrapper<>(new RoleMenu().setRoleId(roleId)));
        return menus.stream().map(e -> String.valueOf(e.getMenuId())).collect(Collectors.toSet());
    }

}
