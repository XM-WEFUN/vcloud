package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.*;
import com.bootvue.admin.service.UserService;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.PlatformType;
import com.bootvue.core.ddo.user.UserDo;
import com.bootvue.core.entity.Role;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.RoleMapperService;
import com.bootvue.core.service.UserMapperService;
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
    private final UserMapperService userMapperService;
    private final RoleMapperService roleMapperService;
    private final HttpServletRequest request;
    private final UserMapper userMapper;
    private final AppConfig appConfig;

    @Override
    public PageOut<List<UserQueryOut>> userList(UserQueryIn param) {
        Page<User> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<UserDo> users = userMapperService.listUsers(page, Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID)), param.getUsername());
        PageOut<List<UserQueryOut>> out = new PageOut<>();

        out.setTotal(users.getTotal());

        out.setRows(users.getRecords().stream().map(e -> new UserQueryOut(e.getId(), e.getUsername(),
                e.getRole(), e.getStatus(), e.getCreateTime())).collect(Collectors.toList()));
        return out;
    }

    @Override
    @CacheEvict(cacheNames = AppConst.USER_CACHE, key = "#param.id", condition = "#param.id != null && #param.id > 0")
    public void addOrUpdateUser(UserIn param) {
        User user;
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));

        if (!ObjectUtils.isEmpty(param.getId()) && !param.getId().equals(0L)) {
            // update user
            user = userMapper.selectById(param.getId());
            if (StringUtils.hasText(param.getPassword())) {
                String password = AppUtil.checkPattern(RsaUtil.getPassword(appConfig, PlatformType.WEB, param.getPassword()), AppConst.PASSWORD_REGEX);
                user.setPassword(DigestUtils.md5Hex(password));
            }
            if (StringUtils.hasText(param.getPhone())) {
                user.setPhone(param.getPhone());
            }
            if (!ObjectUtils.isEmpty(param.getRoleId())) {
                // 验证是否有这个role
                Role role = roleMapperService.findRoleByIdAndTenantId(param.getRoleId(), tenantId);
                if (ObjectUtils.isEmpty(role) || !user.getTenantId().equals(tenantId)) {
                    throw new AppException(RCode.PARAM_ERROR);
                }
                user.setRoleId(role.getId());
            }
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        } else {
            // add
            Assert.notNull(param.getUsername(), RCode.PARAM_ERROR.getMsg());
            Assert.notNull(param.getPassword(), RCode.PARAM_ERROR.getMsg());

            // 用户名是否以及存在
            User existUser = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUsername, param.getUsername()));
            if (!ObjectUtils.isEmpty(existUser)) {
                throw new AppException(RCode.PARAM_ERROR.getCode(), "用户名已存在");
            }

            String password = AppUtil.checkPattern(RsaUtil.getPassword(appConfig, PlatformType.WEB, param.getPassword()), AppConst.PASSWORD_REGEX);

            user = new User(null, param.getUsername(), DigestUtils.md5Hex(password),
                    tenantId, 0L, StringUtils.hasText(param.getPhone()) ? param.getPhone() : "",
                    "", "", "", true, LocalDateTime.now(), null, null
            );
            userMapper.insert(user);
        }
    }

    @Override
    @CacheEvict(cacheNames = AppConst.USER_CACHE, key = "#param.id", condition = "#param.id != null && #param.id > 0")
    public void updateUserStatus(UserIn param) {
        Assert.notNull(param.getId(), RCode.PARAM_ERROR.getMsg());

        User user = userMapper.selectById(param.getId());
        if (!ObjectUtils.isEmpty(user)) {
            user.setUpdateTime(LocalDateTime.now());
            user.setStatus(!user.getStatus());
            userMapper.updateById(user);
        }
    }

    @Override
    @CacheEvict(cacheNames = AppConst.USER_CACHE, key = "#param.id", condition = "#param.id != null && #param.id > 0")
    public void updateSelfInfo(UserIn param) {
        User user = userMapper.selectById(param.getId());
        Assert.notNull(user, RCode.PARAM_ERROR.getMsg());

        String password = AppUtil.checkPattern(RsaUtil.getPassword(appConfig, PlatformType.WEB, param.getPassword()), AppConst.PASSWORD_REGEX);
        user.setPassword(DigestUtils.md5Hex(password));
        userMapper.updateById(user);
    }

    @Override
    @CacheEvict(cacheNames = AppConst.USER_CACHE, key = "#param.userId")
    public void updateUserRole(UserRoleIn param) {
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));
        Role role = roleMapperService.findRoleByNameAndTenantId(param.getRoleName(), tenantId);
        Assert.notNull(role, RCode.PARAM_ERROR.getMsg());

        User user = userMapper.selectById(param.getUserId());
        if (user.getRoleId() < 0 || user.getRoleId().equals(role.getId())) {
            return;
        }
        user.setRoleId(role.getId());
        userMapper.updateById(user);
    }

    @Override
    public RoleUserPageOut<List<RoleUserQueryOut>> roleUserList(RoleUserQueryIn param) {
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));

        Page<User> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<User> users = userMapper.selectPage(page, new QueryWrapper<User>()
                .lambda().eq(User::getTenantId, tenantId).isNull(User::getDeleteTime)
        );

        RoleUserPageOut<List<RoleUserQueryOut>> out = new RoleUserPageOut<>();

        out.setTotal(users.getTotal());

        out.setRows(users.getRecords().stream().map(e -> new RoleUserQueryOut(e.getId(), e.getUsername())).collect(Collectors.toList()));

        if (StringUtils.hasText(param.getRoleName())) {
            // 此角色下已有用户id集合
            Set<Long> ids = userMapperService.listUsersByRoleName(tenantId, param.getRoleName());
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

        // 更新user role_id
        userMapperService.updateUserRoles(param.getSelectedKeys(), param.getUnSelectedKeys(), param.getRoleId(), tenantId);
    }

}
