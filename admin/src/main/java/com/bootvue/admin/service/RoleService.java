package com.bootvue.admin.service;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface RoleService {
    PageOut<List<RoleListOut>> listRole(RoleListIn param, AppUser user);

    void addOrUpdateRole(RoleIn param, AppUser user);

    void deleteRole(RoleIn param, AppUser user);

    void assignUser(RoleAdminIn param, AppUser user);

    void assignMenu(RoleMenuIn param, AppUser user);

    List<RoleListOut> listAllRole(AppUser user);

    List<String> listRoleIdByAdminId(AdminIn param, AppUser user);
}
