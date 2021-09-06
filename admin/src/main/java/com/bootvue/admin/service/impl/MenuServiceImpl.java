package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.service.MenuService;
import com.bootvue.core.dto.menu.MenuListDo;
import com.bootvue.core.entity.Menu;
import com.bootvue.core.entity.RoleMenu;
import com.bootvue.core.mapper.MenuMapper;
import com.bootvue.core.mapper.RoleMenuMapper;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.service.MenuMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuServiceImpl implements MenuService {
    private final MenuMapperService menuMapperService;
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public PageOut<List<MenuListOut>> listMenu(MenuListIn param) {
        IPage<MenuListDo> menus = menuMapperService.listMenu(new Page<>(param.getCurrent(), param.getPageSize()));

        PageOut<List<MenuListOut>> out = new PageOut<>();
        out.setTotal(menus.getTotal());
        out.setRows(menus.getRecords().stream().map(e -> new MenuListOut(e.getId(), e.getTitle(),
                e.getSort(), e.getKey(), e.getPath(), e.getIcon(), e.getPId(), e.getParent(), e.getShow(), e.getDefaultSelect(),
                e.getDefaultOpen())).collect(Collectors.toList()));
        return out;
    }

    @Override
    public List<MenuParentList> listMenuParent() {
        List<Menu> menus = menuMapperService.listMenuParent();
        return menus.stream().map(e -> new MenuParentList(e.getId(), e.getTitle())).collect(Collectors.toList());
    }

    @Override
    public void delMenu(Long id) {
        // menu与role_menu都删除
        Menu menu = menuMapper.selectById(id);
        menuMapper.deleteById(menu);
        roleMenuMapper.delete(new QueryWrapper<RoleMenu>()
                .lambda()
                .eq(RoleMenu::getMenuId, id)
        );
    }

    @Override
    public void addOrUpdateMenu(MenuIn param) {
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
            menuMapper.updateById(menu);
        } else {
            // 新增
            menuMapper.insert(new Menu(null, param.getTitle(), param.getSort(),
                    param.getKey(), param.getPath(), StringUtils.hasText(param.getIcon()) ? param.getIcon() : "",
                    param.getP_id(), param.getShow(), param.getDefaultSelect(), param.getDefaultOpen()));
        }
    }

    @Override
    public List<Long> listMenuByRole(RoleIn param) {
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>()
                .lambda()
                .eq(RoleMenu::getRoleId, param.getId())
        );
        return roleMenus.stream().map(e -> e.getMenuId()).collect(Collectors.toList());
    }
}
