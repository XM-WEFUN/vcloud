package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.menu.dto.MenuIn;
import com.bootvue.admin.controller.menu.dto.MenuItemOut;
import com.bootvue.admin.controller.menu.dto.MenuOut;
import com.bootvue.admin.controller.menu.dto.MenuQueryIn;
import com.bootvue.admin.service.MenuService;
import com.bootvue.core.ddo.menu.MenuTenantDo;
import com.bootvue.core.entity.Menu;
import com.bootvue.core.mapper.MenuMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.RoleMenuActionMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuServiceImpl implements MenuService {
    private final MenuMapper menuMapper;
    private final RoleMenuActionMapperService roleMenuActionMapperService;

    @Override
    public List<MenuItemOut> listByTenant(Long tenantId) {
        List<Menu> menus = menuMapper.selectList(new QueryWrapper<Menu>().lambda().eq(Menu::getTenantId, tenantId).eq(Menu::getPId, 0).orderByAsc(Menu::getSort));
        return menus.stream().map(e -> new MenuItemOut(e.getId(), e.getTitle())).collect(Collectors.toList());
    }

    @Override
    public PageOut<List<MenuOut>> getMenuList(MenuQueryIn param) {
        Page<MenuTenantDo> page = new Page(param.getCurrent(), param.getPageSize());

        IPage<MenuTenantDo> menus = menuMapper.listMenus(page);
        PageOut<List<MenuOut>> out = new PageOut<>();
        out.setTotal(menus.getTotal());

        out.setRows(menus.getRecords().stream().map(e -> {
            MenuOut item = new MenuOut();
            BeanUtils.copyProperties(e, item);
            return item;
        }).collect(Collectors.toList()));
        return out;
    }

    @Override
    public void addOrUpdateMenu(MenuIn param) {
        Menu exist = menuMapper.selectOne(new QueryWrapper<Menu>().lambda().eq(Menu::getTenantId, param.getTenantId()).eq(Menu::getKey, param.getKey()));

        if (!ObjectUtils.isEmpty(param.getId()) && param.getId().compareTo(0L) > 0) {
            // update
            Menu menu = menuMapper.selectById(param.getId());
            if (!ObjectUtils.isEmpty(exist) && !exist.getId().equals(menu.getId())) {
                throw new AppException(RCode.PARAM_ERROR.getCode(), "key已存在");
            }

            menu.setTitle(param.getTitle());
            menu.setSort(param.getSort());
            menu.setKey(param.getKey());
            menu.setPath(param.getPath());
            menu.setIcon(StringUtils.hasText(param.getIcon()) ? param.getIcon() : "");
            menu.setPId(ObjectUtils.isEmpty(param.getPid()) ? 0 : param.getPid());
            menu.setActions(param.getActions());
            menu.setDefaultSelect(param.getDefaultSelect() == 1);
            menu.setDefaultOpen(param.getDefaultOpen() == 1);

            menuMapper.updateById(menu);
        } else {
            // add
            Assert.isNull(exist, "key已存在");
            menuMapper.insert(new Menu(null, param.getTenantId(), param.getTitle(), param.getSort(), param.getKey(),
                    param.getPath(), StringUtils.hasText(param.getIcon()) ? param.getIcon() : "", ObjectUtils.isEmpty(param.getPid()) ? 0 : param.getPid(),
                    param.getActions(), param.getDefaultSelect() == 1, param.getDefaultOpen() == 1));
        }
    }

    @Override
    public void deleteMenu(Long id) {
        menuMapper.deleteById(id);
        // role_menu_actions
        roleMenuActionMapperService.delByMenuId(id);
    }
}
