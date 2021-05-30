package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.*;
import com.bootvue.admin.service.UserService;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.PlatformType;
import com.bootvue.core.ddo.admin.AdminDo;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.entity.Role;
import com.bootvue.core.mapper.AdminMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.AdminMapperService;
import com.bootvue.core.service.RoleMapperService;
import com.bootvue.core.util.AppUtil;
import com.bootvue.core.util.RsaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final AdminMapperService adminMapperService;
    private final RoleMapperService roleMapperService;
    private final HttpServletRequest request;
    private final AdminMapper adminMapper;
    private final AppConfig appConfig;

    @Override
    public PageOut<List<UserQueryOut>> userList(UserQueryIn param) {
        Page<Admin> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<AdminDo> users = adminMapperService.listAdmins(page, Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID)), param.getUsername());
        PageOut<List<UserQueryOut>> out = new PageOut<>();

        out.setTotal(users.getTotal());

        out.setRows(users.getRecords().stream().map(e -> new UserQueryOut(e.getId(), e.getUsername(),
                e.getRole(), e.getStatus(), e.getCreateTime())).collect(Collectors.toList()));
        return out;
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id", condition = "#param.id != null && #param.id > 0")
    public void addOrUpdateUser(UserIn param) {
        Admin admin;
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));

        if (!ObjectUtils.isEmpty(param.getId()) && !param.getId().equals(0L)) {
            // update user
            admin = adminMapper.selectById(param.getId());
            Assert.state(admin.getTenantId().equals(tenantId), RCode.ACCESS_DENY.getMsg());
            if (StringUtils.hasText(param.getPassword())) {
                String password = AppUtil.checkPattern(RsaUtil.getPassword(appConfig, PlatformType.WEB, param.getPassword()), AppConst.PASSWORD_REGEX);
                admin.setPassword(DigestUtils.md5Hex(password));
            }
            if (StringUtils.hasText(param.getPhone())) {
                // 当前租户下是否已存在该手机号用户
                Admin existAdmin = adminMapperService.findByPhoneAndTenantId(param.getPhone(), tenantId);
                Assert.isNull(existAdmin, "手机号已存在");
                admin.setPhone(param.getPhone());
            }
            if (!ObjectUtils.isEmpty(param.getRoleId())) {
                // 验证是否有这个role
                Role role = roleMapperService.findRoleByIdAndTenantId(param.getRoleId(), tenantId);
                if (ObjectUtils.isEmpty(role) || !admin.getTenantId().equals(tenantId)) {
                    throw new AppException(RCode.PARAM_ERROR);
                }
                admin.setRoleId(role.getId());
            }
            admin.setUpdateTime(LocalDateTime.now());
            adminMapper.updateById(admin);
        } else {
            // add
            Assert.notNull(param.getUsername(), RCode.PARAM_ERROR.getMsg());
            Assert.notNull(param.getPassword(), RCode.PARAM_ERROR.getMsg());

            // 用户名是否以及存在
            Admin existUser = adminMapper.selectOne(new QueryWrapper<Admin>().lambda().eq(Admin::getUsername, param.getUsername()).eq(Admin::getTenantId, tenantId));
            if (!ObjectUtils.isEmpty(existUser)) {
                throw new AppException(RCode.PARAM_ERROR.getCode(), "用户名已存在");
            }
            // 当前租户下是否已存在此手机号用户
            if (StringUtils.hasText(param.getPhone())) {
                Admin existAdmin = adminMapperService.findByPhoneAndTenantId(param.getPhone(), tenantId);
                Assert.isNull(existAdmin, "手机号已存在");
            }

            String password = AppUtil.checkPattern(RsaUtil.getPassword(appConfig, PlatformType.WEB, param.getPassword()), AppConst.PASSWORD_REGEX);

            admin = new Admin(null, param.getUsername(), StringUtils.hasText(param.getPhone()) ? param.getPhone() : "", DigestUtils.md5Hex(password),
                    tenantId, 0L, "", true, LocalDateTime.now(), null, null
            );
            adminMapper.insert(admin);
        }
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id", condition = "#param.id != null && #param.id > 0")
    public void updateUserStatus(UserIn param) {
        Assert.notNull(param.getId(), RCode.PARAM_ERROR.getMsg());

        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));
        Admin admin = adminMapper.selectById(param.getId());
        if (!ObjectUtils.isEmpty(admin)) {
            Assert.state(admin.getTenantId().equals(tenantId), RCode.ACCESS_DENY.getMsg());
            admin.setUpdateTime(LocalDateTime.now());
            admin.setStatus(!admin.getStatus());
            adminMapper.updateById(admin);
        }
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id", condition = "#param.id != null && #param.id > 0")
    public void updateSelfInfo(UserIn param) {
        Admin admin = adminMapper.selectById(param.getId());
        Assert.notNull(admin, RCode.PARAM_ERROR.getMsg());
        Assert.state(admin.getTenantId().equals(request.getHeader(AppConst.HEADER_TENANT_ID)), RCode.ACCESS_DENY.getMsg());

        String password = AppUtil.checkPattern(RsaUtil.getPassword(appConfig, PlatformType.WEB, param.getPassword()), AppConst.PASSWORD_REGEX);
        admin.setPassword(DigestUtils.md5Hex(password));
        adminMapper.updateById(admin);
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.userId")
    public void updateUserRole(UserRoleIn param) {
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));
        Role role = roleMapperService.findRoleByNameAndTenantId(param.getRoleName(), tenantId);
        Assert.notNull(role, RCode.PARAM_ERROR.getMsg());
        Assert.state(role.getTenantId().equals(tenantId), RCode.ACCESS_DENY.getMsg());

        Admin admin = adminMapper.selectById(param.getUserId());
        Assert.state(admin.getTenantId().equals(tenantId), RCode.ACCESS_DENY.getMsg());
        if (admin.getRoleId().equals(role.getId())) {
            return;
        }
        admin.setRoleId(role.getId());
        adminMapper.updateById(admin);
    }

    @Override
    public RoleUserPageOut<List<RoleUserQueryOut>> roleUserList(RoleUserQueryIn param) {
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));

        Page<Admin> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<Admin> users = adminMapper.selectPage(page, new QueryWrapper<Admin>()
                .lambda().eq(Admin::getTenantId, tenantId).isNull(Admin::getDeleteTime)
        );

        RoleUserPageOut<List<RoleUserQueryOut>> out = new RoleUserPageOut<>();

        out.setTotal(users.getTotal());

        out.setRows(users.getRecords().stream().map(e -> new RoleUserQueryOut(e.getId(), e.getUsername())).collect(Collectors.toList()));

        if (StringUtils.hasText(param.getRoleName())) {
            // 此角色下已有用户id集合
            Set<Long> ids = adminMapperService.listAdminsByRoleName(tenantId, param.getRoleName());
            out.setKeys(ids);
        }

        return out;
    }

    @Override
    @Transactional
    public void updateUserRoles(UserRolesIn param) {
        log.info("用户: {} 批量修改用户角色, 角色id: {}, 绑定用户: {}, 取消绑定: {}", request.getHeader(AppConst.HEADER_USERNAME), param.getRoleId(), param.getSelectedKeys(), param.getUnSelectedKeys());
        // 验证role是否存在
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));
        Role role = roleMapperService.findRoleByIdAndTenantId(param.getRoleId(), tenantId);
        Assert.notNull(role, "role不存在");
        Assert.state(role.getTenantId().equals(tenantId), RCode.ACCESS_DENY.getMsg());
        // 更新user role_id
        adminMapperService.updateAdminRoles(param.getSelectedKeys(), param.getUnSelectedKeys(), param.getRoleId(), tenantId);
    }

}
