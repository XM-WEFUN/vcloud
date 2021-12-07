package com.bootvue.admin.service;

import com.bootvue.admin.dto.TenantIn;
import com.bootvue.admin.dto.TenantOut;
import com.bootvue.admin.dto.TenantQueryIn;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.PageOut;

import java.util.List;

public interface TenantService {
    void addOrUpdate(TenantIn param);

    PageOut<List<TenantOut>> list(TenantQueryIn param);

    void updateStatus(Long id);

    void delete(Long id);

    List<TenantOut> listAll(AppUser user);
}
