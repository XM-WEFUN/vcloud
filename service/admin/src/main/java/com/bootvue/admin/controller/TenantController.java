package com.bootvue.admin.controller;

import com.bootvue.admin.dto.Id;
import com.bootvue.admin.dto.TenantIn;
import com.bootvue.admin.dto.TenantOut;
import com.bootvue.admin.dto.TenantQueryIn;
import com.bootvue.admin.service.TenantService;
import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.PageOut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tenant")
@Api(tags = "租户管理")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuth(superOnly = true, hasRole = "admin")
public class TenantController {
    private final TenantService tenantService;

    @PostMapping("/add")
    @ApiOperation("新增租户")
    public void add(@Valid @RequestBody TenantIn param) {
        tenantService.addOrUpdate(param);
    }

    @PostMapping("/list")
    @ApiOperation("租户列表")
    public PageOut<List<TenantOut>> list(@RequestBody TenantQueryIn param) {
        return tenantService.list(param);
    }

    @PostMapping("/update")
    @ApiOperation("更新租户信息")
    public void update(@RequestBody TenantIn param) {
        tenantService.addOrUpdate(param);
    }

    @PostMapping("/status")
    @ApiOperation("修改租户状态")
    public void updateStatus(@RequestBody Id param) {
        tenantService.updateStatus(param.getId());
    }

    @PostMapping("/delete")
    @ApiOperation("删除租户")
    public void delete(@RequestBody Id param) {
        tenantService.delete(param.getId());
    }

    @PostMapping("/list_all")
    @ApiOperation("所有租户")
    @PreAuth(superOnly = false)
    public List<TenantOut> listAll(AppUser user) {
        return tenantService.listAll(user);
    }

}
