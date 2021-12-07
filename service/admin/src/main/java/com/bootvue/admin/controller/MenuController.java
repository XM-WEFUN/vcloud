package com.bootvue.admin.controller;

import com.bootvue.admin.dto.Id;
import com.bootvue.admin.dto.MenuItem;
import com.bootvue.admin.dto.TypeIn;
import com.bootvue.admin.service.MenuService;
import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/menu")
@Api(tags = "菜单管理")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuth(superOnly = true, hasRole = "admin")
public class MenuController {

    private final MenuService menuService;

    @ApiOperation("菜单列表")
    @PostMapping("/list")
    @PreAuth(superOnly = false)
    public List<MenuItem> list(@RequestBody TypeIn param, AppUser user) {
        return menuService.list(param.getType(), user);
    }

    @ApiModelProperty("新增菜单")
    @PostMapping("/add")
    public void add(@Valid @RequestBody MenuItem param, BindingResult result) {
        R.handleErr(result);
        menuService.addOrUpdate(param);
    }

    @ApiModelProperty("更新菜单")
    @PostMapping("/update")
    public void update(@Valid @RequestBody MenuItem param, BindingResult result) {
        R.handleErr(result);
        menuService.addOrUpdate(param);
    }

    @ApiModelProperty("删除菜单")
    @PostMapping("/delete")
    public void delete(@RequestBody Id param) {
        menuService.delete(param.getId());
    }

    @ApiModelProperty("某个角色拥有的菜单/按钮 id集合")
    @PostMapping("/list_by_role")
    @PreAuth(superOnly = false)
    public Set<String> listByRole(@RequestBody Id param, AppUser user) {
        return menuService.listByRole(param.getId(), user);
    }
}
