package com.bootvue.admin.controller.setting;

import com.bootvue.admin.controller.setting.dto.TenantIn;
import com.bootvue.admin.controller.setting.dto.TenantListIn;
import com.bootvue.admin.controller.setting.dto.TenantListOut;
import com.bootvue.admin.service.TenantService;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.PageOut;
import com.bootvue.db.entity.Tenant;
import com.bootvue.web.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/tenant")
@Api(tags = "租户管理相关接口")
public class TenantController {
    private final TenantService tenantService;

    @PostMapping("/list")
    @ApiOperation("租户列表")
    public PageOut<List<TenantListOut>> listTenant(@RequestBody TenantListIn param, AppUser user) {
        return tenantService.listTenant(param, user);
    }

    @PostMapping("/list_all_tenant")
    @ApiOperation("所有租户")
    public List<Tenant> listTenant(AppUser user) {
        return tenantService.listAllTenant(user);
    }

    @PostMapping("/add")
    @ApiOperation("新增租户")
    @PreAuth(superOnly = true)
    public void addTenant(@RequestBody TenantIn param) {
        tenantService.addTenant(param);
    }

    @PostMapping("/update")
    @ApiOperation("更新租户信息")
    @PreAuth(superOnly = true)
    public void updateTenant(@RequestBody TenantIn param) {
        tenantService.updateTenant(param);
    }

    @PostMapping("/del")
    @ApiOperation("删除租户")
    @PreAuth(superOnly = true)
    public void delTenant(@RequestBody TenantIn param) {
        tenantService.delTenant(param);
    }
}
