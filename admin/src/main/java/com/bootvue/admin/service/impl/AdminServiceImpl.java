package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.service.AdminService;
import com.bootvue.admin.service.mapper.AdminMapperService;
import com.bootvue.admin.service.mapper.RoleAdminMapperService;
import com.bootvue.admin.service.mapper.RoleMapperService;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.PlatformType;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.util.AppUtil;
import com.bootvue.core.util.RsaUtil;
import com.bootvue.db.entity.Admin;
import com.bootvue.db.entity.Role;
import com.bootvue.db.entity.RoleAdmin;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminServiceImpl implements AdminService {
    private final AdminMapperService adminMapperService;
    private final RoleMapperService roleMapperService;
    private final AppConfig appConfig;
    private final RoleAdminMapperService roleAdminMapperService;

    @Override
    public PageOut<List<AdminListOut>> listAdmin(AdminListIn param, AppUser user) {
        Page<Admin> page = new Page<>(param.getCurrent(), param.getPageSize());

        Long tenantId = AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) ?
                (ObjectUtils.isEmpty(param.getTenantId()) ? null : param.getTenantId())
                : user.getTenantId();

        IPage<AdminListOut> admins = adminMapperService.list(page, param.getUsername(), param.getPhone(), tenantId);

        PageOut<List<AdminListOut>> out = new PageOut<>();
        out.setTotal(admins.getTotal());
        out.setRows(admins.getRecords().stream().map(e -> AdminListOut.builder()
                .id(e.getId())
                .username(e.getUsername())
                .phone(e.getPhone())
                .status(e.getStatus())
                .tenantId(e.getTenantId())
                .tenantName(e.getTenantName())
                .roles(e.getRoles())
                .createTime(e.getCreateTime())
                .deleteTime(e.getDeleteTime()).build()).collect(Collectors.toList()));
        return out;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, allEntries = true)
    public void addOrUpdateAdmin(AdminIn param, AppUser user) {
        PlatformType platform = PlatformType.getPlatform(param.getPlatform());

        if (param.getId() != null && param.getId().compareTo(0L) > 0) {
            // 更新
            Admin admin = adminMapperService.getById(param.getId());
            Assert.notNull(admin, "参数错误");

            if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && !admin.getTenantId().equals(user.getTenantId())) {
                throw new AppException(RCode.ACCESS_DENY);
            }
            Admin existAdmin;
            if (StringUtils.hasText(param.getUsername()) && !param.getUsername().trim().equals(admin.getUsername())) {
                AppUtil.checkPattern(param.getUsername(), AppConst.ACCOUNT_REGEX);
                existAdmin = adminMapperService.getOne(Wrappers.lambdaQuery(new Admin().setTenantId(user.getTenantId()).setUsername(param.getUsername().trim())));
                Assert.isNull(existAdmin, "用户名已存在");
                admin.setUsername(param.getUsername().trim());
            }

            if (StringUtils.hasText(param.getPhone()) && !param.getPhone().trim().equals(admin.getPhone())) {
                AppUtil.checkPattern(param.getPhone(), AppConst.PHONE_REGEX);
                existAdmin = adminMapperService.getOne(Wrappers.lambdaQuery(new Admin().setTenantId(user.getTenantId()).setPhone(param.getPhone().trim())));
                Assert.isNull(existAdmin, "手机号已存在");
                admin.setPhone(param.getPhone());
            }

            if (StringUtils.hasText(param.getPassword())) {
                admin.setPassword(DigestUtils.md5Hex(AppUtil.checkPattern(RsaUtil.getPassword(appConfig, platform, param.getPassword()), AppConst.PASSWORD_REGEX)));
            }
            admin.setUpdateTime(LocalDateTime.now());
            adminMapperService.updateById(admin);
        } else {
            // 新增
            AppUtil.checkPattern(param.getUsername(), AppConst.ACCOUNT_REGEX);
            Admin adminQuery = new Admin().setTenantId(AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) ? param.getTenantId() : user.getTenantId()).
                    setUsername(param.getUsername().trim());
            LambdaQueryWrapper<Admin> queryWrapper = Wrappers.lambdaQuery(adminQuery);
            if (StringUtils.hasText(param.getPhone())) {
                queryWrapper.or().eq(Admin::getPhone, param.getPhone().trim());
            }
            Admin existAdmin = adminMapperService.getOne(queryWrapper);
            Assert.isNull(existAdmin, "用户名已存在");
            Admin admin = new Admin(null, AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) ? param.getTenantId() : user.getTenantId(), param.getUsername().trim(),
                    StringUtils.hasText(param.getPhone()) ? AppUtil.checkPattern(param.getPhone(), AppConst.PHONE_REGEX) : "",
                    DigestUtils.md5Hex(AppUtil.checkPattern(RsaUtil.getPassword(appConfig, platform, param.getPassword()), AppConst.PASSWORD_REGEX)),
                    "", true, LocalDateTime.now(), null, null);
            adminMapperService.save(admin);
        }
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id")
    public void updateAdminStatus(AdminIn param, AppUser user) {
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && user.getTenantId().equals(param.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }
        Admin admin = adminMapperService.getById(param.getId());
        if (!ObjectUtils.isEmpty(admin.getDeleteTime())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        admin.setStatus(!admin.getStatus());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapperService.updateById(admin);
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id")
    public void delAdmin(AdminIn param, AppUser user) {
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && user.getTenantId().equals(param.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }
        Admin admin = adminMapperService.getById(param.getId());
        admin.setStatus(false);
        admin.setDeleteTime(LocalDateTime.now());
        adminMapperService.updateById(admin);
    }

    @Override
    public void assignRole(AdminRoleIn param, AppUser user) {
        Long adminId = param.getAdminId();
        Set<Long> ids = param.getIds();

        Admin admin = adminMapperService.getById(adminId);
        Assert.notNull(admin, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && !user.getTenantId().equals(admin.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }

        admin.setUpdateTime(LocalDateTime.now());

        // 为某个用户分配/取消分配 角色
        log.info("admin用户 id: {} 分配角色: {}", adminId, ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 删除此用户对应的所有角色
            roleAdminMapperService.remove(new QueryWrapper<RoleAdmin>().lambda().in(RoleAdmin::getAdminId, adminId));
            return;
        }

        // 验证role id是否都是此用户所属租户下的
        List<Role> roles = roleMapperService.listByIds(ids);
        Set<Long> tenantIds = roles.stream().map(i -> i.getTenantId()).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(tenantIds) || tenantIds.size() != 1 ||
                !tenantIds.stream().findFirst().get().equals(admin.getTenantId())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        // role_admin 用户已有的角色
        List<RoleAdmin> roleAdmins = roleAdminMapperService.list(Wrappers.lambdaQuery(new RoleAdmin().setAdminId(adminId)));
        Set<Long> orignIds = roleAdmins.stream().map(e -> e.getRoleId()).collect(Collectors.toSet());

        // 删除 未选择的角色id
        Sets.SetView<Long> removeIds = Sets.difference(orignIds, ids);
        if (removeIds.size() > 0) {
            roleAdminMapperService.remove(new QueryWrapper<RoleAdmin>()
                    .lambda()
                    .eq(RoleAdmin::getAdminId, adminId)
                    .in(RoleAdmin::getRoleId, removeIds)
            );
        }
        // 插入 之前不存在的角色id
        Sets.SetView<Long> addIds = Sets.difference(ids, orignIds);
        if (addIds.size() > 0) {
            roleAdminMapperService.saveBatch(addIds.stream().map(i -> new RoleAdmin(null, i, adminId)).collect(Collectors.toList()));
        }
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id")
    public void updateSelf(AdminIn param, AppUser user) {
        PlatformType platform = PlatformType.getPlatform(param.getPlatform());

        if (!StringUtils.hasText(param.getPassword()) || !user.getId().equals(param.getId())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        Admin admin = adminMapperService.getById(param.getId());

        if (StringUtils.hasText(param.getPassword())) {
            admin.setPassword(DigestUtils.md5Hex(AppUtil.checkPattern(RsaUtil.getPassword(appConfig, platform, param.getPassword()), AppConst.PASSWORD_REGEX)));
            admin.setUpdateTime(LocalDateTime.now());
        }

        if (StringUtils.hasText(param.getPhone()) && !param.getPhone().equals(admin.getPhone())) {
            admin.setPhone(AppUtil.checkPattern(param.getPhone(), AppConst.PHONE_REGEX));
        }

        adminMapperService.updateById(admin);
    }

    @Override
    public List<String> listAdminIdByRole(RoleIn param, AppUser user) {
        Long roleId = param.getId();
        Assert.notNull(roleId, "参数错误");

        return roleAdminMapperService.list(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getRoleId, roleId))
                .stream().map(i -> String.valueOf(i.getAdminId())).collect(Collectors.toList());
    }
}
