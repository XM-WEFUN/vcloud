package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.AdminListOut;
import com.bootvue.db.entity.Admin;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper extends BaseMapper<Admin> {

    IPage<AdminListOut> listAdmin(Page<Admin> page, @Param("username") String username, @Param("phone") String phone, @Param("tenant_id") Long tenantId);

    Admin findByUserNameOrPhone(@Param("username") String username, @Param("phone") String phone);
}