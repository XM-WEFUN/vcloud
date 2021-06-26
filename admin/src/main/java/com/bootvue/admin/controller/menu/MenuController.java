package com.bootvue.admin.controller.menu;

import com.bootvue.admin.controller.menu.dto.*;
import com.bootvue.admin.service.MenuService;
import com.bootvue.core.result.PageOut;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "菜单管理相关接口")
@RequestMapping("/menu")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuController {
    private final MenuService menuService;


    @PostMapping("/list_by_tenant")
    public List<MenuItemOut> listByTenant(@RequestBody TenantIn param) {
        return menuService.listByTenant(param.getId());
    }

    @PostMapping("/list")
    public PageOut<List<MenuOut>> getMenuList(@RequestBody MenuQueryIn param) {
        return menuService.getMenuList(param);
    }

    @PostMapping("/add")
    public void addMenu(@RequestBody MenuIn param) {
        menuService.addOrUpdateMenu(param);
    }

    @PostMapping("/update")
    public void updateMenu(@RequestBody MenuIn param) {
        menuService.addOrUpdateMenu(param);
    }

    @PostMapping("/delete")
    public void deleteMenu(@RequestBody MenuIn param) {
        menuService.deleteMenu(param.getId());
    }
}
