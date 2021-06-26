package com.bootvue.admin.service;

import com.bootvue.admin.dto.*;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface AdminService {
    PageOut<List<AdminQueryOut>> userList(AdminQueryIn param);

    void addOrUpdateUser(AdminIn param);

    void updateUserStatus(AdminIn param);

    void updateSelfInfo(AdminIn param);

    RoleUserPageOut<List<RoleUserQueryOut>> roleUserList(RoleUserQueryIn param);

    void updateUserRoles(AdminRolesIn param);
}
