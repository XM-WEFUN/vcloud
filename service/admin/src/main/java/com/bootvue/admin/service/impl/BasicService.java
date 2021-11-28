package com.bootvue.admin.service.impl;

import com.bootvue.admin.dto.UserProfile;
import com.bootvue.common.model.AppUser;

public interface BasicService {
    UserProfile userProfile(AppUser user);
}
