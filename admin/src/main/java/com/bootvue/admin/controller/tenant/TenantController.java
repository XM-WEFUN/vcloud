package com.bootvue.admin.controller.tenant;

import com.bootvue.admin.controller.tenant.dto.TenantIn;
import com.bootvue.admin.controller.tenant.dto.TenantOut;
import com.bootvue.admin.controller.tenant.dto.TenantQueryIn;
import com.bootvue.admin.service.TenantService;
import com.bootvue.core.result.PageOut;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "租户管理相关接口")
@RequestMapping("/tenant")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TenantController {
    private final TenantService tenantService;


    @PostMapping("/list")
    public PageOut<List<TenantOut>> getTenantList(@RequestBody TenantQueryIn param) {
        return tenantService.getTenantList(param);
    }

    @PostMapping("/add")
    public void addTenant(@RequestBody TenantIn param) {
        tenantService.addOrUpdateTenant(param);
    }

    @PostMapping("/update")
    public void updateTenant(@RequestBody TenantIn param) {
        tenantService.addOrUpdateTenant(param);
    }

    @PostMapping("/delete")
    public void deleteTenant(@RequestBody TenantIn param) {
        tenantService.deleteTenant(param);
    }
}
