package com.bootvue.admin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.TenantIn;
import com.bootvue.admin.controller.setting.dto.TenantListIn;
import com.bootvue.admin.controller.setting.dto.TenantListOut;
import com.bootvue.admin.service.TenantService;
import com.bootvue.admin.service.mapper.AdminMapperService;
import com.bootvue.admin.service.mapper.RoleMapperService;
import com.bootvue.admin.service.mapper.TenantMapperService;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.db.entity.Admin;
import com.bootvue.db.entity.Role;
import com.bootvue.db.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TenantServiceImpl implements TenantService {
    private final TenantMapperService tenantMapperService;
    private final AdminMapperService adminMapperService;
    private final RoleMapperService roleMapperService;

    @Override
    public PageOut<List<TenantListOut>> listTenant(TenantListIn param, AppUser user) {
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<Tenant>();
        if (StringUtils.hasText(param.getName())) {
            queryWrapper.like(Tenant::getName, param.getName());
        }
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            queryWrapper.eq(Tenant::getId, user.getTenantId());
        }
        IPage<Tenant> tenants = tenantMapperService.page(new Page<>(param.getCurrent(), param.getPageSize()), queryWrapper);
        PageOut<List<TenantListOut>> out = new PageOut<>();

        out.setTotal(tenants.getTotal());
        out.setRows(tenants.getRecords().stream().map(e -> new TenantListOut(e.getId(), e.getName(), e.getCode(), e.getCreateTime(), e.getDeleteTime()))
                .collect(Collectors.toList()));

        return out;
    }

    @Override
    public void addTenant(TenantIn param) {

        Tenant tenant = tenantMapperService.getOne(new QueryWrapper<>(new Tenant().setName(param.getName().trim())));
        Assert.isNull(tenant, "租户名已存在");

        String code = "T" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + RandomUtil.randomNumbers(5);
        tenantMapperService.save(new Tenant(null, code, param.getName().trim(), LocalDateTime.now(), null, null));
    }

    @Override
    public void updateTenant(TenantIn param) {

        Tenant tenant = tenantMapperService.getById(param.getId());
        Assert.notNull(tenant, "参数错误");

        if (tenant.getName().equals(param.getName())) {
            return;
        }

        Tenant existTenant = tenantMapperService.getOne(new QueryWrapper<>(new Tenant().setName(param.getName().trim())));
        Assert.isNull(existTenant, "租户名已存在");

        tenant.setName(param.getName().trim());
        tenantMapperService.updateById(tenant);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, allEntries = true)
    public void delTenant(TenantIn param) {
        Assert.isTrue(!AppConst.ADMIN_TENANT_ID.equals(param.getId()), "运营平台不可删除");
        // 删除tenant admin 相关数据
        Tenant tenant = tenantMapperService.getById(param.getId());
        Assert.notNull(tenant, "参数错误");

        tenant.setDeleteTime(LocalDateTime.now());
        tenantMapperService.updateById(tenant);

        adminMapperService.update(new UpdateWrapper<Admin>()
                .lambda()
                .set(Admin::getStatus, false)
                .set(Admin::getUpdateTime, LocalDateTime.now())
                .set(Admin::getDeleteTime, LocalDateTime.now())
                .eq(Admin::getTenantId, param.getId())
        );
    }

    @Override
    public List<Tenant> listAllTenant(AppUser user) {
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            queryWrapper.eq(Tenant::getId, user.getTenantId());
        }
        return tenantMapperService.list(queryWrapper);
    }
}

