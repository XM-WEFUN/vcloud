package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.service.AdminService;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.PlatformType;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.entity.RoleAdmin;
import com.bootvue.core.mapper.AdminMapper;
import com.bootvue.core.mapper.RoleAdminMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.AdminMapperService;
import com.bootvue.core.util.RsaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminServiceImpl implements AdminService {
    private final AdminMapperService adminMapperService;
    private final AdminMapper adminMapper;
    private final RoleAdminMapper roleAdminMapper;
    private final AppConfig appConfig;
    private final HttpServletRequest request;

    @Override
    public PageOut<List<AdminListOut>> listAdmin(AdminListIn param) {
        Page<Admin> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<Admin> admins = adminMapperService.listAdmin(page, param.getUsername(), param.getPhone());

        PageOut<List<AdminListOut>> out = new PageOut<>();
        out.setTotal(admins.getTotal());
        out.setRows(admins.getRecords().stream().map(e -> AdminListOut.builder()
                .id(e.getId())
                .username(e.getUsername())
                .phone(e.getPhone())
                .status(e.getStatus())
                .createTime(e.getCreateTime())
                .deleteTime(e.getDeleteTime()).build()).collect(Collectors.toList()));
        return out;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, allEntries = true)
    public void addOrUpdateAdmin(AdminIn param) {
        PlatformType platform = PlatformType.getPlatform(Integer.parseInt(request.getHeader(AppConst.HEADER_PLATFORM)));

        if (param.getId() != null && param.getId().compareTo(0L) > 0) {
            // 更新
            Admin admin = adminMapper.selectById(param.getId());
            if (StringUtils.hasText(param.getPhone())) {
                admin.setPhone(param.getPhone());
            }

            if (StringUtils.hasText(param.getPassword())) {
                admin.setPassword(DigestUtils.md5Hex(RsaUtil.getPassword(appConfig, platform, param.getPassword())));
            }
            admin.setUpdateTime(LocalDateTime.now());
            adminMapper.updateById(admin);
        } else {
            // 新增
            Admin existAdmin = adminMapper.findByUserNameOrPhone(param.getUsername().trim(), param.getPhone().trim());
            Assert.isNull(existAdmin, "用户名已存在");
            Admin admin = new Admin(null, param.getUsername().trim(),
                    StringUtils.hasText(param.getPhone()) ? param.getPhone().trim() : "",
                    DigestUtils.md5Hex(RsaUtil.getPassword(appConfig, platform, param.getPassword())),
                    "", true, LocalDateTime.now(), null, null);
            adminMapper.insert(admin);
        }
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id")
    public void updateAdminStatus(AdminIn param) {
        Admin admin = adminMapper.selectById(param.getId());
        if (!ObjectUtils.isEmpty(admin.getDeleteTime())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        admin.setStatus(!admin.getStatus());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.updateById(admin);
    }

    @Override
    @CachePut(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id")
    public void delAdmin(AdminIn param) {
        Admin admin = adminMapper.selectById(param.getId());
        admin.setStatus(false);
        admin.setDeleteTime(LocalDateTime.now());
        adminMapper.updateById(admin);
    }

    @Override
    public void assignRole(AdminRoleIn param) {
        Long adminId = param.getAdminId();
        Set<Long> ids = param.getIds();

        // 为某个用户分配/取消分配 角色
        log.info("admin用户 id: {} 分配角色: {}", adminId, ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 删除此用户对应的所有角色
            roleAdminMapper.delete(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getAdminId, adminId));
            return;
        }

        // 此用户原有 角色信息
        List<RoleAdmin> roleAdmins = roleAdminMapper.selectList(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getAdminId, adminId));

        if (CollectionUtils.isEmpty(roleAdmins)) {
            ids.forEach(e -> roleAdminMapper.insert(new RoleAdmin(null, e, adminId)));
            return;
        }

        // 要新增/删除的 角色id
        // 原有角色id
        Set<Long> orignRoleIds = roleAdmins.stream().map(e -> e.getRoleId()).collect(Collectors.toSet());

        ids.forEach(e -> {
            if (!orignRoleIds.contains(e)) {
                // 新增
                roleAdminMapper.insert(new RoleAdmin(null, e, adminId));
            }
        });

        orignRoleIds.forEach(e -> {
            if (!ids.contains(e)) {
                // 删除
                roleAdminMapper.delete(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getAdminId, adminId).eq(RoleAdmin::getRoleId, e));
            }
        });
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id")
    public void updateSelf(AdminIn param) {
        Long adminId = Long.valueOf(request.getHeader(AppConst.HEADER_USER_ID));
        PlatformType platform = PlatformType.getPlatform(Integer.parseInt(request.getHeader(AppConst.HEADER_PLATFORM)));

        Admin admin = adminMapper.selectById(adminId);
        if (StringUtils.hasText(param.getPassword())) {
            admin.setPassword(DigestUtils.md5Hex(RsaUtil.getPassword(appConfig, platform, param.getPassword())));
            admin.setUpdateTime(LocalDateTime.now());
            adminMapper.updateById(admin);
        }
    }

    @Override
    public List<Long> listAdminIdByRole(RoleIn param) {
        List<RoleAdmin> roleAdmins = roleAdminMapper.selectList(new QueryWrapper<RoleAdmin>()
                .lambda()
                .eq(RoleAdmin::getRoleId, param.getId())
                .orderByAsc(RoleAdmin::getAdminId)
        );

        return roleAdmins.stream().map(e -> e.getAdminId()).collect(Collectors.toList());
    }
}
