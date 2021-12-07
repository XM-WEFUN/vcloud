package com.bootvue.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.dto.UserItem;
import com.bootvue.admin.mapper.UserMapper;
import com.bootvue.common.constant.AppConst;
import com.bootvue.datasource.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapperService extends ServiceImpl<UserMapper, User> implements IService<User> {

    private final UserMapper userMapper;

    @Cacheable(cacheNames = AppConst.ADMIN_CACHE, key = "#id", unless = "#result==null")
    public User findById(Long id) {
        return getById(id);
    }

    public Page<UserItem> users(Page<UserItem> page, Long tenantId, String account, String phone) {
        return userMapper.users(page, tenantId, account, phone);
    }
}
