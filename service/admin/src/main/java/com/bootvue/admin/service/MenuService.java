package com.bootvue.admin.service;

import com.bootvue.admin.dto.MenuItem;
import com.bootvue.common.model.AppUser;

import java.util.List;
import java.util.Set;

public interface MenuService {
    List<MenuItem> list(Integer type, AppUser user);

    void delete(Long id);

    void addOrUpdate(MenuItem param);

    Set<String> listByRole(Long roleId, AppUser user);
}
