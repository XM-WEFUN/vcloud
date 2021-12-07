package com.bootvue.admin.controller;

import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.Id;
import com.bootvue.admin.dto.UserItem;
import com.bootvue.admin.dto.UserQueryIn;
import com.bootvue.admin.service.UserService;
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

@Api(tags = "管理员类用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuth(hasRole = "admin")
public class UserController {

    private final UserService userService;

    @ApiOperation("用户列表")
    @PostMapping("/list")
    public PageOut<List<UserItem>> list(@RequestBody UserQueryIn param, AppUser user) {
        return userService.list(param, user);
    }

    @ApiOperation("新增用户")
    @PostMapping("/add")
    public void add(@RequestBody UserItem param, AppUser user) {
        userService.addOrUpdate(param, user);
    }

    @ApiOperation("更新用户信息")
    @PostMapping("/update")
    public void update(@RequestBody UserItem param, AppUser user) {
        userService.addOrUpdate(param, user);
    }


    @ApiOperation("修改用户状态")
    @PostMapping("/status")
    public void updateStatus(@RequestBody Id param, AppUser user) {
        userService.updateStatus(param.getId(), user);
    }

    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public void delete(@RequestBody Id param, AppUser user) {
        userService.delete(param.getId(), user);
    }

    @PostMapping("/list_by_role")
    @ApiOperation("某个角色拥有的 user_id集合")
    public Set<String> listByRole(@RequestBody Id param, AppUser user) {
        return userService.listByRole(param.getId(), user);
    }

    @PostMapping("/list_by_dept")
    @ApiOperation("某个部门拥有的 user_id集合")
    public Set<String> listByDept(@RequestBody Id param, AppUser user) {
        return userService.listByDept(param.getId(), user);
    }

    @PostMapping("/assign_role")
    @ApiOperation("分配角色")
    public void assignRole(@Valid @RequestBody AssignIn param, AppUser user, BindingResult result) {
        R.handleErr(result);
        userService.assignRole(param, user);
    }

    @PostMapping("/assign_dept")
    @ApiOperation("分配部门")
    public void assignDept(@Valid @RequestBody AssignIn param, AppUser user, BindingResult result) {
        R.handleErr(result);
        userService.assignDept(param, user);
    }
}
