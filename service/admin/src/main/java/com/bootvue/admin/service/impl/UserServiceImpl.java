package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.UserItem;
import com.bootvue.admin.dto.UserQueryIn;
import com.bootvue.admin.service.*;
import com.bootvue.common.config.app.AppConfig;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.PageOut;
import com.bootvue.common.util.AppUtil;
import com.bootvue.common.util.RsaUtil;
import com.bootvue.datasource.entity.*;
import com.bootvue.datasource.type.AccountTypeEnum;
import com.bootvue.datasource.type.GenderEnum;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserServiceImpl implements UserService {

    private final AppConfig appConfig;
    private final UserMapperService userMapperService;
    private final RoleMapperService roleMapperService;
    private final TenantMapperService tenantMapperService;
    private final DeptMapperService deptMapperService;
    private final UserRoleMapperService userRoleMapperService;
    private final UserDeptMapperService userDeptMapperService;

    @Override
    public Set<String> listByRole(Long id, AppUser user) {
        Role role = roleMapperService.getById(id);
        Assert.notNull(role, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Assert.isTrue(role.getTenantId().equals(user.getTenantId()), "参数错误");
        }

        List<UserRole> userRoles = userRoleMapperService.list(new QueryWrapper<>(new UserRole().setRoleId(id)));
        return userRoles.stream().map(e -> String.valueOf(e.getUserId())).collect(Collectors.toSet());
    }

    @Override
    public Set<String> listByDept(Long id, AppUser user) {
        Dept dept = deptMapperService.getById(id);
        Assert.notNull(dept, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Assert.isTrue(dept.getTenantId().equals(user.getTenantId()), "参数错误");
        }

        List<UserDept> userDepts = userDeptMapperService.list(new QueryWrapper<>(new UserDept().setDeptId(id)));
        return userDepts.stream().map(e -> String.valueOf(e.getUserId())).collect(Collectors.toSet());
    }

    @Override
    public PageOut<List<UserItem>> list(UserQueryIn param, AppUser user) {
        Long tenantId = AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) ? param.getTenantId() : user.getTenantId();
        Page<UserItem> pages = userMapperService.users(new Page<>(param.getCurrent(), param.getPageSize()), tenantId, param.getAccount(), param.getPhone());

        return new PageOut<>(pages.getTotal(), pages.getRecords());
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#param.id", condition = "#param.id!=null")
    public void addOrUpdate(UserItem param, AppUser u) {
        User user;
        if (ObjectUtils.isEmpty(param.getId())) {
            // 新增
            Assert.hasText(param.getAccount(), "参数错误");
            Assert.isTrue(AppConst.ADMIN_TENANT_ID.equals(u.getTenantId()) || !u.getTenantId().equals(param.getTenantId()), "参数错误");
            // 验证account是否存在
            User exist = userMapperService.getOne(new QueryWrapper<>(new User().setTenantId(param.getTenantId()).setAccount(param.getAccount())));
            Assert.isNull(exist, "账号已存在");
            user = new User();
            user.setTenantId(param.getTenantId());
            user.setAccount(param.getAccount());
            user.setStatus(true);
            user.setType(AppConst.ADMIN_TENANT_ID.equals(param.getTenantId()) ? AccountTypeEnum.ADMIN : AccountTypeEnum.TENANT_ADMIN);
            user.setCreateTime(LocalDateTime.now());
        } else {
            // 更新
            user = userMapperService.getById(param.getId());
            Assert.isTrue(ObjectUtils.isEmpty(user.getDeleteTime()), "用户不存在");
            user.setUpdateTime(LocalDateTime.now());
        }

        // 校验密码
        if (!StringUtils.hasText(user.getPassword()) || StringUtils.hasText(param.getPassword())) {
            // 新增
            String password = AppUtil.checkPattern(RsaUtil.decrypt(appConfig.getPrivateKey(), param.getPassword()), AppConst.PASSWORD_REGEX);
            user.setPassword(DigestUtils.md5Hex(password));
        }

        user.setNickName(StringUtils.hasText(param.getNickName()) ? param.getNickName() : "");
        user.setPhone(StringUtils.hasText(param.getPhone()) ? param.getPhone() : "");
        user.setCountry(StringUtils.hasText(param.getCountry()) ? param.getCountry() : "");
        user.setProvince(StringUtils.hasText(param.getProvince()) ? param.getProvince() : "");
        user.setCity(StringUtils.hasText(param.getCity()) ? param.getCity() : "");
        user.setRegion(StringUtils.hasText(param.getRegion()) ? param.getRegion() : "");
        user.setAvatar(StringUtils.hasText(param.getAvatar()) ? param.getAvatar() : "");
        user.setGender(ObjectUtils.isEmpty(param.getGender()) ? GenderEnum.UNKNOWN : GenderEnum.find(param.getGender()));

        userMapperService.saveOrUpdate(user);
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#id")
    public void delete(Long id, AppUser u) {
        User user = userMapperService.getById(id);
        Assert.notNull(user, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(u.getTenantId())) {
            Assert.isTrue(u.getTenantId().equals(user.getTenantId()), "参数错误");
        }
        Assert.isTrue(!user.getId().equals(1L), "此用户不可删除");
        user.setStatus(false);
        user.setDeleteTime(LocalDateTime.now());
        log.info("删除用户: {}", user.getAccount());
        userMapperService.updateById(user);
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#id")
    public void updateStatus(Long id, AppUser u) {
        Assert.isTrue(!Long.valueOf(1L).equals(id), "此用户不可禁用");

        User user = userMapperService.getById(id);
        if (!AppConst.ADMIN_TENANT_ID.equals(u.getTenantId())) {
            Assert.isTrue(u.getTenantId().equals(user.getTenantId()), "参数错误");
        }

        user.setStatus(!user.getStatus());
        user.setUpdateTime(LocalDateTime.now());

        userMapperService.updateById(user);
    }

    @Override
    public void assignRole(AssignIn param, AppUser u) {
        // 验证用户
        User user = userMapperService.getById(param.getId());

        Assert.notNull(user, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(u.getTenantId())) {
            Assert.isTrue(user.getTenantId().equals(u.getTenantId()), "参数错误");
        }
        // 分配的角色id
        Set<Long> ids = param.getIds();

        log.info("用户: {} 分配角色: {}", user.getAccount(), ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 清除
            userRoleMapperService.remove(new QueryWrapper<>(new UserRole().setUserId(param.getId())));
            return;
        }

        // 验证 ids是否都属于 此租户
        List<Role> roles = roleMapperService.list(new QueryWrapper<Role>().lambda().in(Role::getId, ids));
        Set<Long> tenants = roles.stream().map(Role::getTenantId).collect(Collectors.toSet());
        Assert.isTrue(tenants.size() == 1 && tenants.contains(user.getTenantId()), "参数错误");

        // 此用户已有 role id
        Set<Long> orignIds = userRoleMapperService.list(new QueryWrapper<>(new UserRole().setUserId(param.getId()))).stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        // 需要删除的 id
        Sets.SetView<Long> removeIds = Sets.difference(orignIds, ids);
        // 需要新增的 id
        Sets.SetView<Long> addIds = Sets.difference(ids, orignIds);

        if (!CollectionUtils.isEmpty(removeIds)) {
            userRoleMapperService.remove(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, param.getId()).in(UserRole::getRoleId, removeIds));
        }

        if (!CollectionUtils.isEmpty(addIds)) {
            userRoleMapperService.saveBatch(addIds.stream().map(e -> new UserRole(null, param.getId(), e)).collect(Collectors.toList()));
        }
    }

    @Override
    public void assignDept(AssignIn param, AppUser u) {
        // 验证用户
        User user = userMapperService.getById(param.getId());

        Assert.notNull(user, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(u.getTenantId())) {
            Assert.isTrue(user.getTenantId().equals(u.getTenantId()), "参数错误");
        }
        // 分配的部门id
        Set<Long> ids = param.getIds();

        log.info("用户: {} 分配部门: {}", user.getAccount(), ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 清除
            userDeptMapperService.remove(new QueryWrapper<>(new UserDept().setUserId(param.getId())));
            return;
        }

        // 验证 ids是否都属于 此租户
        List<Dept> depts = deptMapperService.list(new QueryWrapper<Dept>().lambda().in(Dept::getId, ids));
        Set<Long> tenants = depts.stream().map(Dept::getTenantId).collect(Collectors.toSet());
        Assert.isTrue(tenants.size() == 1 && tenants.contains(user.getTenantId()), "参数错误");

        // 要分配的部门id  --> 需要包含上级id
        ids.addAll(depts.stream().map(e -> e.getPId()).filter(e -> !Long.valueOf(0L).equals(e)).collect(Collectors.toSet()));

        // 此用户已有 dept id
        Set<Long> orignIds = userDeptMapperService.list(new QueryWrapper<>(new UserDept().setUserId(param.getId()))).stream().map(UserDept::getDeptId).collect(Collectors.toSet());

        // 需要删除的 id
        Sets.SetView<Long> removeIds = Sets.difference(orignIds, ids);
        // 需要新增的 id
        Sets.SetView<Long> addIds = Sets.difference(ids, orignIds);

        if (!CollectionUtils.isEmpty(removeIds)) {
            userDeptMapperService.remove(new QueryWrapper<UserDept>().lambda().eq(UserDept::getUserId, param.getId()).in(UserDept::getDeptId, removeIds));
        }

        if (!CollectionUtils.isEmpty(addIds)) {
            userDeptMapperService.saveBatch(addIds.stream().map(e -> new UserDept(null, param.getId(), e)).collect(Collectors.toList()));
        }
    }
}
