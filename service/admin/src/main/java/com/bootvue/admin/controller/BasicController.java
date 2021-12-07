package com.bootvue.admin.controller;

import com.bootvue.admin.dto.UserProfile;
import com.bootvue.admin.dto.UserProfileIn;
import com.bootvue.admin.service.BasicService;
import com.bootvue.common.model.AppUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/basic")
@Api(tags = "基础信息")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BasicController {

    private final BasicService basicService;

    @PostMapping("/user_profile")
    @ApiOperation("用户信息")
    public UserProfile userProfile(AppUser user) {
        return basicService.userProfile(user);
    }

    @PostMapping("/user_profile_update")
    @ApiOperation("更新用户信息")
    public void updateUserProfile(@RequestBody UserProfileIn param, AppUser user) {
        basicService.updateUserProfile(param, user);
    }
}
