package com.bootvue.admin.controller.setting;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.service.AdminService;
import com.bootvue.core.model.AppUser;
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
@RequestMapping("/user")
@Api(tags = "管理员用户相关接口")
public class AdminController {
    private final AdminService adminService;

    @ApiOperation("用户列表")
    @PostMapping("/list")
    public PageOut<List<AdminListOut>> listAdmin(@RequestBody AdminListIn param, AppUser user) {
        return adminService.listAdmin(param, user);
    }

    @ApiOperation("某个角色下的所有用户id")
    @PostMapping("/list_by_role")
    public List<String> listAdminIdByRole(@RequestBody RoleIn param, AppUser user) {
        return adminService.listAdminIdByRole(param, user);
    }

    @ApiOperation("新增用户")
    @PostMapping("/add")
    public void addAdmin(@Valid @RequestBody AdminIn param, AppUser user, BindingResult result) {
        R.handleErr(result);
        adminService.addOrUpdateAdmin(param, user);
    }

    @ApiOperation("更新用户信息")
    @PostMapping("/update")
    public void updateAdmin(@Valid @RequestBody AdminIn param, AppUser user, BindingResult result) {
        R.handleErr(result);
        adminService.addOrUpdateAdmin(param, user);
    }

    @ApiOperation("更新用户自身信息")
    @PostMapping("/update_self")
    public void updateSelf(@RequestBody AdminIn param, AppUser user) {
        adminService.updateSelf(param, user);
    }

    @ApiOperation("更新用户状态")
    @PostMapping("/update_status")
    public void updateAdminStatus(@RequestBody AdminIn param, AppUser user) {
        adminService.updateAdminStatus(param, user);
    }

    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public void delAdmin(@RequestBody AdminIn param, AppUser user) {
        adminService.delAdmin(param, user);
    }

    @ApiOperation("批量分配用户角色")
    @PostMapping("/assign_role")
    public void assignRole(@RequestBody AdminRoleIn param, AppUser user) {
        adminService.assignRole(param, user);
    }
}
