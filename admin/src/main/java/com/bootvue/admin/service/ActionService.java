package com.bootvue.admin.service;

import com.bootvue.admin.controller.action.dto.ActionIn;
import com.bootvue.admin.controller.action.dto.ActionOut;
import com.bootvue.admin.controller.action.dto.ActionQueryIn;
import com.bootvue.admin.dto.ActionItem;
import com.bootvue.admin.dto.RoleActionIn;
import com.bootvue.admin.dto.RoleIn;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface ActionService {
    List<ActionItem> actionList(RoleIn role);

    void updateRoleService(RoleActionIn param);

    PageOut<List<ActionOut>> getActionList(ActionQueryIn param);

    void addOrUpdateAction(ActionIn param);

    void deleteAction(ActionIn param);
}
