package com.bootvue.admin.controller;

import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.Id;
import com.bootvue.admin.dto.RoleItem;
import com.bootvue.admin.dto.RoleQueryIn;
import com.bootvue.admin.service.RoleService;
import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.PageOut;
import com.bootvue.common.result.R;
import io.swagger.annotations.Api;
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

@Api(tags = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuth(hasRole = "admin")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/list")
    @ApiOperation("角色列表")
    public PageOut<List<RoleItem>> list(@RequestBody RoleQueryIn param, AppUser user) {
        return roleService.list(param, user);
    }

    @PostMapping("/add")
    @ApiOperation("新增角色")
    public void add(@RequestBody RoleItem param, AppUser user) {
        roleService.addOrUpdate(param, user);
    }

    @PostMapping("/update")
    @ApiOperation("更新角色")
    public void update(@RequestBody RoleItem param, AppUser user) {
        roleService.addOrUpdate(param, user);
    }

    @PostMapping("/delete")
    @ApiOperation("删除角色")
    public void delete(@RequestBody Id param, AppUser user) {
        roleService.delete(param.getId(), user);
    }

    @PostMapping("/list_by_user")
    @ApiOperation("某个用户拥有的role_id集合")
    public Set<String> listByUser(@RequestBody Id param, AppUser user) {
        return roleService.listByUser(param.getId(), user);
    }

    @PostMapping("/assign_user")
    @ApiOperation("分配用户")
    public void assignUser(@Valid @RequestBody AssignIn param, AppUser user, BindingResult result) {
        R.handleErr(result);
        roleService.assignUser(param, user);
    }

    @PostMapping("/assign_menu")
    @ApiOperation("分配菜单/按钮")
    public void assignMenu(@Valid @RequestBody AssignIn param, AppUser user, BindingResult result) {
        R.handleErr(result);
        roleService.assignMenu(param, user);
    }
}
