package com.bootvue.admin.service;

import com.bootvue.admin.dto.UserIn;
import com.bootvue.admin.dto.UserOut;
import com.bootvue.core.result.PageOut;

import java.util.List;

public interface UserService {
    PageOut<List<UserOut>> userList(UserIn param);
}
