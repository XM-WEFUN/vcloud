package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.admin.dto.DeptItem;
import com.bootvue.datasource.entity.Dept;

import java.util.List;

public interface DeptMapper extends BaseMapper<Dept> {
    List<DeptItem> listByTenantId(Long tenantId);
}
