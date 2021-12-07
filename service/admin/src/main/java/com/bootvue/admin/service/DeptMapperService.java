package com.bootvue.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.dto.DeptItem;
import com.bootvue.admin.mapper.DeptMapper;
import com.bootvue.datasource.entity.Dept;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeptMapperService extends ServiceImpl<DeptMapper, Dept> implements IService<Dept> {

    private final DeptMapper deptMapper;

    public List<DeptItem> listByTenantId(Long tenantId) {
        return deptMapper.listByTenantId(tenantId);
    }
}
