package com.bootvue.admin.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.tenant.dto.TenantIn;
import com.bootvue.admin.controller.tenant.dto.TenantOut;
import com.bootvue.admin.controller.tenant.dto.TenantQueryIn;
import com.bootvue.admin.service.TenantService;
import com.bootvue.core.entity.Tenant;
import com.bootvue.core.mapper.TenantMapper;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.AdminMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TenantServiceImpl implements TenantService {
    private final TenantMapper tenantMapper;
    private final AdminMapperService adminMapperService;

    @Override
    public PageOut<List<TenantOut>> getTenantList(TenantQueryIn param) {
        Page<Tenant> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<Tenant> pages = tenantMapper.getTenantList(page, param.getTenantName());
        PageOut<List<TenantOut>> out = new PageOut<>();

        out.setTotal(pages.getTotal());
        out.setRows(pages.getRecords().stream().map(e -> new TenantOut(e.getId(), e.getCode(), e.getName(), e.getCreateTime())).collect(Collectors.toList()));
        return out;
    }

    @Override
    public void addOrUpdateTenant(TenantIn param) {
        if (!ObjectUtils.isEmpty(param.getId()) && param.getId().compareTo(0L) > 0) {
            // update
            Tenant tenant = tenantMapper.selectById(param.getId());
            Assert.notNull(tenant, RCode.PARAM_ERROR.getMsg());
            if (tenant.getName().equals(param.getName())) {
                return;
            }
            Tenant existTenant = tenantMapper.selectOne(new QueryWrapper<Tenant>().lambda().eq(Tenant::getName, param.getName()));
            Assert.isNull(existTenant, "租户名已存在");
            tenant.setName(param.getName());

            tenantMapper.updateById(tenant);
        } else {
            // add
            Tenant existTenant = tenantMapper.selectOne(new QueryWrapper<Tenant>().lambda().eq(Tenant::getName, param.getName()));
            Assert.isNull(existTenant, "租户名已存在");
            tenantMapper.insert(new Tenant(null, IdUtil.fastSimpleUUID().toUpperCase(), param.getName(), LocalDateTime.now(), null));
        }
    }

    @Override
    @Transactional
    public void deleteTenant(TenantIn param) {
        Tenant tenant = tenantMapper.selectById(param.getId());
        Assert.notNull(tenant, RCode.PARAM_ERROR.getMsg());
        tenant.setDeleteTime(LocalDateTime.now());
        tenantMapper.updateById(tenant);

        // 禁用 此租户下的所有管理员用户
        adminMapperService.updateAdminStatusByTenantId(param.getId(), 0);
    }
}
