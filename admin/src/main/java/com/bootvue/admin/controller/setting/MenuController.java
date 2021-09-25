package com.bootvue.admin.controller.setting;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.service.MenuService;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.PageOut;
import com.bootvue.web.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
@Api(tags = "菜单相关接口")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuController {
    private final MenuService menuService;

    @ApiOperation("菜单列表")
    @PostMapping("/list")
    public PageOut<List<MenuListOut>> listMenu(@RequestBody MenuListIn param, AppUser user) {
        return menuService.listMenu(param, user);
    }

    @ApiOperation("某个角色对应的菜单id")
    @PostMapping("/list_by_role")
    public List<String> listMenuByRole(@RequestBody RoleIn param, AppUser user) {
        return menuService.listMenuByRole(param, user);
    }

    @ApiOperation("所有一级菜单信息")
    @PostMapping("/list/parent")
    public List<MenuParentList> listMenuParent() {
        return menuService.listMenuParent();
    }

    @ApiOperation("新增菜单")
    @PostMapping("/add")
    @PreAuth(superOnly = true)
    public void addMenu(@RequestBody MenuIn param) {
        menuService.addOrUpdateMenu(param);
    }

    @ApiOperation("更新菜单")
    @PostMapping("/update")
    @PreAuth(superOnly = true)
    public void updateMenu(@RequestBody MenuIn param) {
        menuService.addOrUpdateMenu(param);
    }

    @ApiOperation("删除菜单")
    @PostMapping("/delete")
    @PreAuth(superOnly = true)
    public void delMenu(@RequestBody MenuIn param) {
        menuService.delMenu(param.getId());
    }
}
