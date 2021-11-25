package com.bootvue.auth.endpoints;

import com.bootvue.auth.dto.Captcha;
import com.bootvue.auth.dto.ClientInfo;
import com.bootvue.auth.service.AuthService;
import com.bootvue.common.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/oauth2")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "用户认证")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/captcha")
    @ApiOperation("图形验证码")
    public Captcha getCaptcha(@Valid ClientInfo param, BindingResult result) {
        R.handleErr(result);
        return authService.getCaptcha(param);
    }
}
