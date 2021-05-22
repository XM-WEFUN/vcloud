package com.bootvue.admin.service;

import com.bootvue.admin.dto.ActionItem;
import com.bootvue.admin.dto.RoleActionIn;
import com.bootvue.admin.dto.RoleIn;

import java.util.List;

public interface ActionService {
    List<ActionItem> actionList(RoleIn role);

    void updateRoleService(RoleActionIn param);
}
