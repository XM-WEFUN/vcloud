package com.bootvue.admin.service;

import com.bootvue.admin.controller.menu.dto.MenuIn;
import com.bootvue.admin.controller.menu.dto.MenuItemOut;
import com.bootvue.admin.controller.menu.dto.MenuOut;
import com.bootvue.admin.controller.menu.dto.MenuQueryIn;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface MenuService {
    List<MenuItemOut> listByTenant(Long id);

    PageOut<List<MenuOut>> getMenuList(MenuQueryIn param);

    void addOrUpdateMenu(MenuIn param);

    void deleteMenu(Long id);
}
