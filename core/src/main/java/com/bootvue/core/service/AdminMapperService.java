package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminMapperService {
    private final AdminMapper adminMapper;

    public Admin findByPhoneAndTenantCode(String phone, String tenantCode) {
        return adminMapper.findByPhoneAndTenantCode(phone, tenantCode);
    }

    public Admin findById(Long id) {
        return adminMapper.selectOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getId, id)
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime)
        );
    }

    public Admin findByUsernameAndPassword(String username, String password, String tenantCode) {
        return adminMapper.findByUsernameAndPassword(username, password, tenantCode);
    }
}
