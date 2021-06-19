package com.bootvue.admin.controller.user;

import com.bootvue.admin.dto.*;
import com.bootvue.admin.service.AdminService;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "用户管理相关接口")
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final AdminService userService;
    private final HttpServletRequest request;

    @PostMapping("/list")
    @ApiOperation("查询-用户列表")
    public PageOut<List<UserQueryOut>> userList(@RequestBody UserQueryIn param) {
        return userService.userList(param);
    }

    @PostMapping("/list_by_role")
    @ApiOperation("查询-某个角色下包含的用户列表")
    public RoleUserPageOut<List<RoleUserQueryOut>> roleUserList(@RequestBody RoleUserQueryIn param) {
        return userService.roleUserList(param);
    }

    @PostMapping("/add")
    @ApiOperation("新增管理员用户")
    public void addUser(@RequestBody UserIn param) {
        userService.addOrUpdateUser(param);
    }

    @PostMapping("/update")
    @ApiOperation("更新管理员用户信息")
    public void updateUser(@RequestBody UserIn param) {
        userService.addOrUpdateUser(param);
    }

    @PostMapping("/update_status")
    @ApiOperation("更新管理员用户状态")
    public void updateUserStatus(@RequestBody UserIn param) {
        userService.updateUserStatus(param);
    }

    @PostMapping("/update_self")
    @ApiOperation("更新用户自身信息")
    public void updateSelfInfo(@RequestBody UserIn param) {
        Long userId = Long.valueOf(request.getHeader(AppConst.HEADER_USER_ID));
        param.setId(userId);
        userService.updateSelfInfo(param);
    }

    @PostMapping("/update_roles")
    @ApiOperation("批量修改用户角色")
    public void updateRoles(@RequestBody @Valid UserRolesIn param, BindingResult result) {
        R.handleErr(result);
        userService.updateUserRoles(param);
    }
}
