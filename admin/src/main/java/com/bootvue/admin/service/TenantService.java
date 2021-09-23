package com.bootvue.admin.service;

import com.bootvue.admin.controller.setting.dto.TenantIn;
import com.bootvue.admin.controller.setting.dto.TenantListIn;
import com.bootvue.admin.controller.setting.dto.TenantListOut;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.PageOut;
import com.bootvue.db.entity.Tenant;

import java.util.List;

public interface TenantService {
    PageOut<List<TenantListOut>> listTenant(TenantListIn param, AppUser user);

    void addTenant(TenantIn param, AppUser user);

    void updateTenant(TenantIn param, AppUser user);

    void delTenant(TenantIn param, AppUser user);

    List<Tenant> listAllTenant(AppUser user);
}
