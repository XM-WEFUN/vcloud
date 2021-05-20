package com.bootvue.admin.service;

import com.bootvue.admin.dto.UserIn;
import com.bootvue.admin.dto.UserQueryIn;
import com.bootvue.admin.dto.UserQueryOut;
import com.bootvue.admin.dto.UserRoleIn;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface UserService {
    PageOut<List<UserQueryOut>> userList(UserQueryIn param);

    void addOrUpdateUser(UserIn param);

    void updateUserStatus(UserIn param);

    void updateSelfInfo(UserIn param);

    void updateUserRole(UserRoleIn param);
}
