package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.TenantIn;
import com.bootvue.admin.dto.TenantOut;
import com.bootvue.admin.dto.TenantQueryIn;
import com.bootvue.admin.service.TenantMapperService;
import com.bootvue.admin.service.TenantService;
import com.bootvue.admin.service.UserMapperService;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.PageOut;
import com.bootvue.common.result.RCode;
import com.bootvue.datasource.entity.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantMapperService tenantMapperService;
    private final UserMapperService userMapperService;

    @Override
    public void addOrUpdate(TenantIn param) {
        if (!ObjectUtils.isEmpty(param.getId())) {
            // 更新
            Tenant tenant = tenantMapperService.getById(param.getId());
            Assert.isTrue(ObjectUtils.isEmpty(tenant.getDeleteTime()), "租户不存在");
            if (StringUtils.hasText(param.getName()) && !param.getName().trim().equals(tenant.getName())) {
                tenant.setName(param.getName());
            }
            if (StringUtils.hasText(param.getContactName()) && !param.getContactName().trim().equals(tenant.getContactName())) {
                tenant.setContactName(param.getContactName());
            }
            if (StringUtils.hasText(param.getContactPhone()) && !param.getContactPhone().trim().equals(tenant.getContactPhone())) {
                tenant.setContactPhone(param.getContactPhone());
            }
            tenant.setUpdateTime(LocalDateTime.now());
            tenantMapperService.updateById(tenant);
        } else {
            // 新增
            if (!StringUtils.hasText(param.getName()) || !StringUtils.hasText(param.getContactName()) || !StringUtils.hasText(param.getContactPhone())) {
                throw new AppException(RCode.PARAM_ERROR);
            }
            String code = "V" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + RandomStringUtils.randomNumeric(4);
            tenantMapperService.save(new Tenant(null, code, param.getName(), param.getContactName(), param.getContactPhone(), true, LocalDateTime.now(), null, null));
        }
    }

    @Override
    public PageOut<List<TenantOut>> list(TenantQueryIn param) {
        Page<Tenant> page = new Page<>(param.getCurrent(), param.getPageSize());
        LambdaQueryWrapper<Tenant> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(param.getCode())) {
            queryWrapper.eq(Tenant::getCode, param.getCode());
        }
        if (StringUtils.hasText(param.getName())) {
            queryWrapper.like(Tenant::getName, param.getName());
        }
        Page<Tenant> pages = tenantMapperService.page(page, queryWrapper);

        List<TenantOut> outs = pages.getRecords().stream().map(e -> {
            TenantOut item = new TenantOut(e.getId(), e.getCode(), e.getName(), e.getContactName(), e.getContactPhone(), e.getStatus());
            item.setId(e.getId());
            item.setCreateTime(e.getCreateTime());
            item.setUpdateTime(e.getUpdateTime());
            item.setDeleteTime(e.getDeleteTime());
            return item;
        }).collect(Collectors.toList());
        return new PageOut<>(pages.getTotal(), outs);
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, allEntries = true)
    public void updateStatus(Long id) {
        Tenant tenant = tenantMapperService.getById(id);
        Assert.isTrue(!AppConst.ADMIN_TENANT_ID.equals(tenant.getId()), "平台租户状态不可修改");
        Assert.isNull(tenant.getDeleteTime(), "租户已被删除");
        tenant.setStatus(!tenant.getStatus());

        tenant.setUpdateTime(LocalDateTime.now());
        tenantMapperService.saveOrUpdate(tenant);
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, allEntries = true)
    public void delete(Long id) {
        Tenant tenant = tenantMapperService.getById(id);
        Assert.isTrue(!AppConst.ADMIN_TENANT_ID.equals(tenant.getId()), "平台租户不可删除");
        tenant.setStatus(false);
        tenant.setUpdateTime(LocalDateTime.now());
        tenant.setDeleteTime(LocalDateTime.now());
        log.info("租户: {} id: {} 被删除.....", tenant.getName(), id);
        tenantMapperService.saveOrUpdate(tenant);
    }

    @Override
    public List<TenantOut> listAll(AppUser user) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tenant::getStatus, true).isNull(Tenant::getDeleteTime);

        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            wrapper.eq(Tenant::getId, user.getTenantId());
        }

        List<Tenant> tenants = tenantMapperService.list(wrapper);
        return tenants.stream().map(e -> new TenantOut(e.getId(), null, e.getName(), null, null, null)).collect(Collectors.toList());
    }
}
