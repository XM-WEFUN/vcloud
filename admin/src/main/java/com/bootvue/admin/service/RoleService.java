package com.bootvue.admin.service;

import com.bootvue.admin.dto.RoleIn;
import com.bootvue.admin.dto.RoleQueryIn;
import com.bootvue.admin.dto.RoleQueryOut;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface RoleService {
    PageOut<List<RoleQueryOut>> roleList(RoleQueryIn param);

    void addOrUpdateRole(RoleIn param);

    void delRole(RoleIn param);
}
