package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.admin.dto.MenuItem;
import com.bootvue.admin.dto.UserProfile;
import com.bootvue.admin.service.RoleMenuMapperService;
import com.bootvue.admin.service.UserMapperService;
import com.bootvue.admin.service.UserRoleMapperService;
import com.bootvue.common.model.AppUser;
import com.bootvue.datasource.entity.Menu;
import com.bootvue.datasource.entity.User;
import com.bootvue.datasource.entity.UserRole;
import com.bootvue.datasource.type.MenuTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BasicServiceImpl implements BasicService {

    private final UserMapperService userMapperService;
    private final UserRoleMapperService userRoleMapperService;
    private final RoleMenuMapperService roleMenuMapperService;

    @Override
    public UserProfile userProfile(AppUser user) {
        User u = userMapperService.findById(user.getId());

        UserProfile profile = new UserProfile();

        profile.setAccount(u.getAccount());
        profile.setNickName(u.getNickName());
        profile.setAvatar(u.getAvatar());
        profile.setGender(u.getGender().getDesc());
        profile.setPhone(u.getPhone());

        // 菜单  按钮  权限

        // 1 用户拥有的所有角色id
        List<UserRole> userRoles = userRoleMapperService.list(new QueryWrapper<>(new UserRole().setUserId(user.getId())));
        if (CollectionUtils.isEmpty(userRoles)) {
            return profile;
        }
        Set<Long> roleIds = userRoles.stream().map(e -> e.getRoleId()).collect(Collectors.toSet());

        // 2 这些角色拥有的菜单集合
        Set<Menu> menus = roleMenuMapperService.getMenusByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(menus)) {
            return profile;
        }

        profile.setMenus(handleMenus(menus));

        return profile;
    }

    /**
     * 处理菜单
     *
     * @param menus
     * @return
     */
    private List<MenuItem> handleMenus(Set<Menu> menus) {
        // 1 顶级菜单
        List<Menu> parents = menus.stream().filter(e -> e.getPId().equals(0L) && e.getType().equals(MenuTypeEnum.MENU))
                .sorted(Comparator.comparingInt(Menu::getSort))
                .collect(Collectors.toList());

        // 2 菜单&按钮
        List<MenuItem> items = parents.stream().map(e -> {
            // 遍历-->children
            return new MenuItem(e.getTitle(), e.getKey(), e.getPath(), e.getIcon(), e.getAction(), e.getShow(), e.getDefaultSelect(), e.getDefaultOpen(), handleChildren(e, menus));
        }).collect(Collectors.toList());

        return items;
    }

    /**
     * 子级菜单
     *
     * @param parent
     * @param menus
     * @return
     */
    private List<MenuItem> handleChildren(Menu parent, Set<Menu> menus) {
        // 子菜单
        List<Menu> subMenus = menus.stream().filter(e -> e.getPId().equals(parent.getId()))
                .sorted(Comparator.comparingInt(Menu::getSort))
                .collect(Collectors.toList());

        // 子菜单 --> 按钮
        return subMenus.stream().map(e -> {
            List<MenuItem> sb = handleSubButton(e, menus);
            return new MenuItem(e.getTitle(), e.getKey(), parent.getPath() + e.getPath(), e.getIcon(),
                    sb.stream().map(i -> i.getAction()).collect(Collectors.joining(","))
                    , e.getShow(), e.getDefaultSelect(), e.getDefaultOpen(), null);
        }).collect(Collectors.toList());
    }

    /**
     * 按钮
     *
     * @param item
     * @param menus
     * @return
     */
    private List<MenuItem> handleSubButton(Menu item, Set<Menu> menus) {
        return menus.stream().filter(e -> e.getPId().equals(item.getId()))
                .sorted(Comparator.comparingInt(Menu::getSort))
                .map(e -> new MenuItem(e.getTitle(), e.getKey(), e.getPath(), e.getIcon(), e.getAction(), e.getShow(), e.getDefaultSelect(), e.getDefaultOpen(), null))
                .collect(Collectors.toList());
    }
}
