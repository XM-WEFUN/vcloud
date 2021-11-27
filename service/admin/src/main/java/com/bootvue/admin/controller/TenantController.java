package com.bootvue.admin.controller;

import com.bootvue.admin.dto.TenantIn;
import com.bootvue.admin.service.TenantService;
import com.bootvue.common.annotation.PreAuth;
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

@RestController
@RequestMapping("/tenant")
@Api(tags = "租户管理")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuth(superOnly = true, hasRole = "admin")
public class TenantController {
    private final TenantService tenantService;

    @PostMapping("/add")
    @ApiOperation("新增租户")
    public void add(@Valid @RequestBody TenantIn param, BindingResult result) {
        R.handleErr(result);
        tenantService.add(param);
    }
}
