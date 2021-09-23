package com.bootvue.admin.service;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface AdminService {
    PageOut<List<AdminListOut>> listAdmin(AdminListIn param, AppUser user);

    void addOrUpdateAdmin(AdminIn param, AppUser user);

    void updateAdminStatus(AdminIn param, AppUser user);

    void delAdmin(AdminIn param, AppUser user);

    void assignRole(AdminRoleIn param, AppUser user);

    void updateSelf(AdminIn param, AppUser user);

    List<Long> listAdminIdByRole(RoleIn param, AppUser user);
}
