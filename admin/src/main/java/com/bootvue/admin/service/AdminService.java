package com.bootvue.admin.service;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface AdminService {
    PageOut<List<AdminListOut>> listAdmin(AdminListIn param);

    void addOrUpdateAdmin(AdminIn param);

    void updateAdminStatus(AdminIn param);

    void delAdmin(AdminIn param);

    void assignRole(AdminRoleIn param);

    void updateSelf(AdminIn param);

    List<Long> listAdminIdByRole(RoleIn param);
}
