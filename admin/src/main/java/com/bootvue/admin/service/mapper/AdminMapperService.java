package com.bootvue.admin.service.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.controller.setting.dto.AdminListOut;
import com.bootvue.admin.mapper.AdminMapper;
import com.bootvue.db.entity.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminMapperService extends ServiceImpl<AdminMapper, Admin> implements IService<Admin> {
    private final AdminMapper adminMapper;

    public IPage<AdminListOut> list(Page<Admin> page, String username, String phone, Long tenantId) {
        return adminMapper.listAdmin(page, username, phone, tenantId);
    }
}
