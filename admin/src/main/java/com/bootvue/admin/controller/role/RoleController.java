package com.bootvue.admin.controller.role;

import com.bootvue.admin.dto.RoleIn;
import com.bootvue.admin.dto.RoleQueryIn;
import com.bootvue.admin.dto.RoleQueryOut;
import com.bootvue.admin.service.RoleService;
import com.bootvue.core.result.PageOut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "角色管理相关接口")
@RequestMapping("/role")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/list")
    @ApiOperation("查询-角色列表")
    public PageOut<List<RoleQueryOut>> userList(@RequestBody RoleQueryIn param) {
        return roleService.roleList(param);
    }


    @PostMapping("/add")
    @ApiOperation("新增角色")
    public void addUser(@RequestBody RoleIn param) {
        roleService.addOrUpdateRole(param);
    }

    @PostMapping("/update")
    @ApiOperation("更新角色信息")
    public void updateRole(@RequestBody RoleIn param) {
        roleService.addOrUpdateRole(param);
    }

    @PostMapping("/delete")
    @ApiOperation("删除某个角色")
    public void delRole(@RequestBody RoleIn param) {
        roleService.delRole(param);
    }
}
