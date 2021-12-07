package com.bootvue.admin.service;

import cn.hutool.core.lang.tree.Tree;
import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.DeptItem;
import com.bootvue.common.model.AppUser;

import java.util.List;
import java.util.Set;

public interface DeptService {
    List<Tree<String>> list(Long tenantId, AppUser user);

    void delete(Long id, AppUser user);

    void addOrUpdate(DeptItem param, AppUser user);

    void assignUser(AssignIn param, AppUser user);

    Set<String> listByUser(Long userId, AppUser user);
}
