package com.bootvue.admin.controller.user;

import com.bootvue.admin.dto.UserIn;
import com.bootvue.admin.dto.UserQueryIn;
import com.bootvue.admin.dto.UserQueryOut;
import com.bootvue.admin.service.UserService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "用户管理相关接口")
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;
    private final HttpServletRequest request;

    @PostMapping("/list")
    @ApiOperation("查询-用户列表")
    public PageOut<List<UserQueryOut>> userList(@RequestBody UserQueryIn param) {
        return userService.userList(param);
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

    @PostMapping("/updateStatus")
    @ApiOperation("更新管理员用户状态")
    public void updateUserStatus(@RequestBody UserIn param) {
        userService.updateUserStatus(param);
    }

    @PostMapping("/updateSelf")
    @ApiOperation("更新用户自身信息")
    public void updateSelfInfo(@RequestBody UserIn param) {
        Long userId = Long.valueOf(request.getHeader("user_id"));
        param.setId(userId);
        userService.updateSelfInfo(param);
    }
}
