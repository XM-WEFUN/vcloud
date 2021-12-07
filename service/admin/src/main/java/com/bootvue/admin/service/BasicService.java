package com.bootvue.admin.service;

import com.bootvue.admin.dto.UserProfile;
import com.bootvue.admin.dto.UserProfileIn;
import com.bootvue.common.model.AppUser;

public interface BasicService {
    UserProfile userProfile(AppUser user);

    void updateUserProfile(UserProfileIn param, AppUser user);
}
