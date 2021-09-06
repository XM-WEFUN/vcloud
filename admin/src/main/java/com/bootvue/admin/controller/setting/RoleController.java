package com.bootvue.admin.controller.setting;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.service.RoleService;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.R;
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

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/role")
@Api(tags = "角色相关接口")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/list")
    @ApiOperation("角色列表")
    public PageOut<List<RoleListOut>> listRole(@RequestBody RoleListIn param) {
        return roleService.listRole(param);
    }

    @PostMapping("/list_by_admin")
    @ApiOperation("某个用户对应的角色id")
    public List<Long> listRoleIdByAdminId(@RequestBody AdminIn param) {
        return roleService.listRoleIdByAdminId(param);
    }

    @PostMapping("/list_all")
    @ApiOperation("所有角色列表")
    public List<RoleListOut> listAllRole() {
        return roleService.listAllRole();
    }

    @PostMapping("/add")
    @ApiOperation("新增角色")
    public void addRole(@RequestBody RoleIn param) {
        roleService.addOrUpdateRole(param);
    }

    @PostMapping("/update")
    @ApiOperation("更新角色")
    public void updateRole(@RequestBody RoleIn param) {
        roleService.addOrUpdateRole(param);
    }

    @PostMapping("/delete")
    @ApiOperation("删除角色")
    public void deleteRole(@RequestBody RoleIn param) {
        roleService.deleteRole(param);
    }

    @PostMapping("/assign_user")
    @ApiOperation("为某个角色批量分配/取消分配 用户")
    public void assignUser(@Valid @RequestBody RoleAdminIn param, BindingResult result) {
        R.handleErr(result);
        roleService.assignUser(param);
    }

    @PostMapping("/assign_menu")
    @ApiOperation("为某个角色批量分配/取消分配 菜单")
    public void assignMenu(@Valid @RequestBody RoleMenuIn param, BindingResult result) {
        R.handleErr(result);
        roleService.assignMenu(param);
    }
}
