package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.entity.Tenant;
import org.apache.ibatis.annotations.Param;

public interface TenantMapper extends BaseMapper<Tenant> {
    IPage<Tenant> getTenantList(Page<Tenant> page, @Param("name") String tenantName);
}