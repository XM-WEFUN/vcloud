package com.bootvue.core.service;

import com.bootvue.core.mapper.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleMenuMapperService {
    private final RoleMenuMapper roleMenuMapper;

    public List<Long> findMenuIdsByAdminId(Long adminId) {
        return roleMenuMapper.findMenuIdsByAdminId(adminId);
    }
}
