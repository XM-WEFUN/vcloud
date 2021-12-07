package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.admin.dto.MenuItem;
import com.bootvue.admin.dto.UserProfile;
import com.bootvue.admin.dto.UserProfileIn;
import com.bootvue.admin.service.BasicService;
import com.bootvue.admin.service.RoleMenuMapperService;
import com.bootvue.admin.service.UserMapperService;
import com.bootvue.admin.service.UserRoleMapperService;
import com.bootvue.admin.util.MenuUtil;
import com.bootvue.common.config.app.AppConfig;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.util.AppUtil;
import com.bootvue.common.util.RsaUtil;
import com.bootvue.datasource.entity.User;
import com.bootvue.datasource.entity.UserRole;
import com.bootvue.datasource.type.GenderEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BasicServiceImpl implements BasicService {

    private final AppConfig appConfig;
    private final UserMapperService userMapperService;
    private final UserRoleMapperService userRoleMapperService;
    private final RoleMenuMapperService roleMenuMapperService;

    @Override
    public UserProfile userProfile(AppUser user) {
        User u = userMapperService.findById(user.getId());

        UserProfile profile = new UserProfile();

        profile.setTenantId(user.getTenantId());
        profile.setType(u.getType().getValue());
        profile.setAccount(u.getAccount());
        profile.setNickName(u.getNickName());
        profile.setAvatar(u.getAvatar());
        profile.setGender(u.getGender().getValue());
        profile.setPhone(u.getPhone());

        // 菜单  按钮  权限

        // 1 用户拥有的所有角色id
        List<UserRole> userRoles = userRoleMapperService.list(new QueryWrapper<>(new UserRole().setUserId(user.getId())));
        if (CollectionUtils.isEmpty(userRoles)) {
            return profile;
        }
        Set<Long> roleIds = userRoles.stream().map(e -> e.getRoleId()).collect(Collectors.toSet());

        // 2 这些角色拥有的菜单集合
        Set<MenuItem> menus = roleMenuMapperService.getMenusByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(menus)) {
            return profile;
        }

        profile.setMenus(MenuUtil.handleMenus(menus, 0));

        return profile;
    }

    @Override
    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, key = "#u.id")
    public void updateUserProfile(UserProfileIn param, AppUser u) {
        User user = userMapperService.getById(u.getId());
        if (StringUtils.hasText(param.getNickName())) {
            user.setNickName(param.getNickName());
        }
        if (!ObjectUtils.isEmpty(param.getGender())) {
            user.setGender(GenderEnum.find(param.getGender()));
        }
        if (StringUtils.hasText(param.getPhone())) {
            user.setPhone(AppUtil.checkPattern(param.getPhone(), AppConst.PHONE_REGEX));
        }
        if (StringUtils.hasText(param.getPassword())) {
            String password = AppUtil.checkPattern(RsaUtil.decrypt(appConfig.getPrivateKey(), param.getPassword()), AppConst.PASSWORD_REGEX);
            user.setPassword(DigestUtils.md5Hex(password));
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapperService.updateById(user);
    }

}
