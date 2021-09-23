package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.mapper.MenuMapper;
import com.bootvue.admin.model.MenuListDo;
import com.bootvue.admin.service.MenuService;
import com.bootvue.admin.service.mapper.MenuMapperService;
import com.bootvue.admin.service.mapper.RoleMapperService;
import com.bootvue.admin.service.mapper.RoleMenuMapperService;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.db.entity.Menu;
import com.bootvue.db.entity.Role;
import com.bootvue.db.entity.RoleMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuServiceImpl implements MenuService {
    private final MenuMapper menuMapper;
    private final MenuMapperService menuMapperService;
    private final RoleMenuMapperService roleMenuMapperService;
    private final RoleMapperService roleMapperService;

    @Override
    public PageOut<List<MenuListOut>> listMenu(MenuListIn param, AppUser user) {
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }
        IPage<MenuListDo> menus = menuMapper.listMenu(new Page<>(param.getCurrent(), param.getPageSize()));

        PageOut<List<MenuListOut>> out = new PageOut<>();
        out.setTotal(menus.getTotal());
        out.setRows(menus.getRecords().stream().map(e -> new MenuListOut(e.getId(), e.getTitle(),
                e.getSort(), e.getKey(), e.getPath(), e.getIcon(), e.getPId(), e.getParent(), e.getShow(), e.getDefaultSelect(),
                e.getDefaultOpen())).collect(Collectors.toList()));
        return out;
    }

    @Override
    public List<MenuParentList> listMenuParent() {
        List<Menu> menus = menuMapperService.list(new QueryWrapper<>(
                new Menu().setPId(0L).setShow(true)
        ));
        return menus.stream().map(e -> new MenuParentList(e.getId(), e.getTitle())).collect(Collectors.toList());
    }

    @Override
    public void delMenu(Long id, AppUser user) {
        // menu与role_menu都删除
        Menu menu = menuMapperService.getById(id);
        menuMapperService.removeById(menu);

        roleMenuMapperService.remove(new QueryWrapper<RoleMenu>()
                .lambda()
                .eq(RoleMenu::getMenuId, id)
        );
    }

    @Override
    public void addOrUpdateMenu(MenuIn param, AppUser user) {
        Assert.isTrue(AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()), "参数错误");

        if (param.getId() != null && param.getId().compareTo(0L) > 0) {
            // 更新
            Menu menu = menuMapper.selectById(param.getId());
            menu.setTitle(param.getTitle());
            menu.setSort(param.getSort());
            menu.setKey(param.getKey());
            menu.setPath(param.getPath());
            menu.setIcon(StringUtils.hasText(param.getIcon()) ? param.getIcon() : "");
            menu.setPId(param.getP_id());
            menu.setShow(param.getShow());
            menu.setDefaultSelect(param.getDefaultSelect());
            menu.setDefaultOpen(param.getDefaultOpen());
            menuMapperService.updateById(menu);
        } else {
            // 新增
            menuMapperService.save(new Menu(null, param.getTitle(), param.getSort(),
                    param.getKey(), param.getPath(), StringUtils.hasText(param.getIcon()) ? param.getIcon() : "",
                    param.getP_id(), param.getShow(), param.getDefaultSelect(), param.getDefaultOpen()));
        }
    }

    @Override
    public List<Long> listMenuByRole(RoleIn param, AppUser user) {
        Role role = roleMapperService.getById(param.getId());
        Assert.notNull(role, "参数错误");

        Assert.isTrue(role.getTenantId().equals(user.getTenantId()), "参数错误");

        List<RoleMenu> roleMenus = roleMenuMapperService.list(new QueryWrapper<RoleMenu>()
                .lambda()
                .eq(RoleMenu::getRoleId, param.getId())
        );
        return roleMenus.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
    }
}
