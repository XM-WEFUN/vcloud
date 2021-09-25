package com.bootvue.admin.service;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface MenuService {
    PageOut<List<MenuListOut>> listMenu(MenuListIn param, AppUser user);

    List<MenuParentList> listMenuParent();

    void delMenu(Long id);

    void addOrUpdateMenu(MenuIn param);

    List<String> listMenuByRole(RoleIn param, AppUser user);
}
