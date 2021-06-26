package com.bootvue.admin.service;

import com.bootvue.admin.controller.tenant.dto.TenantIn;
import com.bootvue.admin.controller.tenant.dto.TenantOut;
import com.bootvue.admin.controller.tenant.dto.TenantQueryIn;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface TenantService {
    PageOut<List<TenantOut>> getTenantList(TenantQueryIn param);

    void addOrUpdateTenant(TenantIn param);

    void deleteTenant(TenantIn param);
}
