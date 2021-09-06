package com.bootvue.admin.service;

import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface RoleService {
    PageOut<List<RoleListOut>> listRole(RoleListIn param);

    void addOrUpdateRole(RoleIn param);

    void deleteRole(RoleIn param);

    void assignUser(RoleAdminIn param);

    void assignMenu(RoleMenuIn param);

    List<RoleListOut> listAllRole();

    List<Long> listRoleIdByAdminId(AdminIn param);
}
