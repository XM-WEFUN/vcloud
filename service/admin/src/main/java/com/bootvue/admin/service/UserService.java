package com.bootvue.admin.service;

import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.UserItem;
import com.bootvue.admin.dto.UserQueryIn;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.PageOut;

import java.util.List;
import java.util.Set;

public interface UserService {
    Set<String> listByRole(Long id, AppUser user);

    Set<String> listByDept(Long id, AppUser user);

    PageOut<List<UserItem>> list(UserQueryIn param, AppUser user);

    void addOrUpdate(UserItem param, AppUser user);

    void delete(Long id, AppUser user);

    void updateStatus(Long id, AppUser user);

    void assignRole(AssignIn param, AppUser user);

    void assignDept(AssignIn param, AppUser user);
}
