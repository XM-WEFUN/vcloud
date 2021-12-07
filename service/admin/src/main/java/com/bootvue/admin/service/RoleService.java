package com.bootvue.admin.service;

import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.RoleItem;
import com.bootvue.admin.dto.RoleQueryIn;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.PageOut;

import java.util.List;
import java.util.Set;

public interface RoleService {
    PageOut<List<RoleItem>> list(RoleQueryIn param, AppUser user);

    void addOrUpdate(RoleItem param, AppUser user);

    void delete(Long id, AppUser user);

    void assignMenu(AssignIn param, AppUser user);

    void assignUser(AssignIn param, AppUser user);

    Set<String> listByUser(Long userId, AppUser user);
}
