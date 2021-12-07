package com.bootvue.admin.util;

import com.bootvue.admin.dto.MenuItem;
import com.bootvue.datasource.type.MenuTypeEnum;
import com.google.common.base.Joiner;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MenuUtil {
    /**
     * 处理菜单
     *
     * @param menus
     * @param type  0 普通模式 1 返回菜单树型结构(包含按钮) 2 返回菜单树型结构(不包含按钮)
     * @return
     */
    public static List<MenuItem> handleMenus(Collection<MenuItem> menus, int type) {
        // 1 顶级菜单
        List<MenuItem> parents = menus.stream().filter(e -> e.getPid().equals(0L) && e.getType().equals(MenuTypeEnum.MENU.getValue()))
                .sorted(Comparator.comparingInt(MenuItem::getSort))
                .collect(Collectors.toList());

        // 2 菜单&按钮
        List<MenuItem> items = parents.stream().map(e -> {
            // 遍历-->children
            return new MenuItem(e.getId(), e.getTitle(), e.getPid(), e.getPtitle(), e.getKey(), e.getPath(), e.getIcon(), e.getSort(), e.getType(), e.getAction(), e.getShow(), e.getDefaultSelect(), e.getDefaultOpen(),
                    type == 0 ? handleChildren(e, menus) : handleChildren2(e, menus, type));
        }).collect(Collectors.toList());

        return items;
    }

    private static List<MenuItem> handleChildren2(MenuItem parent, Collection<MenuItem> menus, int type) {
        // 子菜单
        List<MenuItem> subMenus = menus.stream().filter(e -> e.getPid().equals(parent.getId()))
                .sorted(Comparator.comparingInt(MenuItem::getSort))
                .collect(Collectors.toList());

        // 子菜单 --> 按钮
        return subMenus.stream().map(e -> new MenuItem(e.getId(), e.getTitle(), e.getPid(), e.getPtitle(), e.getKey(), e.getPath(), e.getIcon(), e.getSort(), e.getType(),
                e.getAction(), e.getShow(), e.getDefaultSelect(), e.getDefaultOpen(), type == 1 ? handleSubButton(e, menus) : null)).collect(Collectors.toList());
    }

    /**
     * 子级菜单
     *
     * @param parent
     * @param menus
     * @return
     */
    private static List<MenuItem> handleChildren(MenuItem parent, Collection<MenuItem> menus) {
        // 子菜单
        List<MenuItem> subMenus = menus.stream().filter(e -> e.getPid().equals(parent.getId()))
                .sorted(Comparator.comparingInt(MenuItem::getSort))
                .collect(Collectors.toList());

        // 子菜单 --> 按钮
        return subMenus.stream().map(e -> {
            List<MenuItem> sb = handleSubButton(e, menus);
            return new MenuItem(e.getId(), e.getTitle(), e.getPid(), e.getPtitle(), e.getKey(), parent.getPath() + e.getPath(), e.getIcon(), e.getSort(), e.getType(),
                    Joiner.on(",").skipNulls().join(e.getAction(), sb.stream().map(i -> i.getAction()).collect(Collectors.joining(",")))
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
    private static List<MenuItem> handleSubButton(MenuItem item, Collection<MenuItem> menus) {
        return menus.stream().filter(e -> e.getPid().equals(item.getId()))
                .sorted(Comparator.comparingInt(MenuItem::getSort))
                .map(e -> new MenuItem(e.getId(), e.getTitle(), e.getPid(), e.getPtitle(), e.getKey(), e.getPath(), e.getIcon(), e.getSort(), e.getType(), e.getAction(), e.getShow(), e.getDefaultSelect(), e.getDefaultOpen(), null))
                .collect(Collectors.toList());
    }
}
