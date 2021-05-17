package com.bootvue.admin.controller.user;

import com.bootvue.admin.dto.UserIn;
import com.bootvue.admin.dto.UserOut;
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

    @PostMapping("/list")
    @ApiOperation("查询-用户列表")
    public PageOut<List<UserOut>> userList(@RequestBody UserIn param, HttpServletRequest request) {
        return userService.userList(param);
    }
    
}
