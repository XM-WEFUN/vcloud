package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminMapperService {
    private final AdminMapper adminMapper;


    public Admin findByPhone(String phone) {
        return adminMapper.selectOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getPhone, phone)
        );
    }

    @Cacheable(cacheNames = AppConst.ADMIN_CACHE, key = "#id", unless = "#result==null")
    public Admin findById(Long id) {
        return adminMapper.selectOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getId, id)
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime)
        );
    }

    public Admin findByUsernameAndPassword(String username, String password) {
        return adminMapper.selectOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getUsername, username)
                .eq(Admin::getPassword, password)
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime)
        );
    }

    public IPage<Admin> listAdmin(Page<Admin> page, String username, String phone) {
        return adminMapper.listAdmin(page, username, phone);
    }
}
