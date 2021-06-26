package com.bootvue.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.action.dto.ActionIn;
import com.bootvue.admin.controller.action.dto.ActionOut;
import com.bootvue.admin.controller.action.dto.ActionQueryIn;
import com.bootvue.admin.dto.*;
import com.bootvue.admin.service.ActionService;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.Action;
import com.bootvue.core.entity.Menu;
import com.bootvue.core.entity.Role;
import com.bootvue.core.entity.RoleMenuAction;
import com.bootvue.core.mapper.ActionMapper;
import com.bootvue.core.mapper.MenuMapper;
import com.bootvue.core.mapper.RoleMapper;
import com.bootvue.core.mapper.RoleMenuActionMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.ActionMapperService;
import com.bootvue.core.service.RoleMapperService;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ActionServiceImpl implements ActionService {
    private final MenuMapper menuMapper;
    private final RoleMapperService roleMapperService;
    private final RoleMenuActionMapper roleMenuActionMapper;
    private final ActionMapperService actionMapperService;
    private final HttpServletRequest request;
    private final ActionMapper actionMapper;
    private final RoleMapper roleMapper;

    @Override
    public List<ActionItem> actionList(RoleIn param) {
        Assert.notNull(param.getId(), "参数错误");
        Role role = roleMapper.selectById(param.getId());
        Assert.notNull(role, "参数错误");

        // 菜单&action权限信息
        List<ActionItem> out = new ArrayList<>();

        // 父级菜单
        List<Menu> menus = menuMapper.selectList(new QueryWrapper<Menu>().lambda().eq(Menu::getPId, 0L).eq(Menu::getTenantId, role.getTenantId()).orderByAsc(Menu::getSort));
        menus.stream().forEach(e -> {
            ActionItem item = getAction(param.getId(), e);
            // 子菜单
            List<Menu> subMenus = menuMapper.selectList(new QueryWrapper<Menu>().lambda().eq(Menu::getPId, e.getId()).eq(Menu::getTenantId, role.getTenantId()).orderByAsc(Menu::getSort));
            List<ActionItem> children = new ArrayList<>();
            subMenus.stream().forEach(i -> children.add(getAction(param.getId(), i)));
            item.setChildren(children);

            out.add(item);
        });

        return out;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = AppConst.ACTION_CACHE, key = "#param.roleId")
    public void updateRoleService(RoleActionIn param) {
        log.info("用户: {} 更新了角色: {} 的actions权限: {}", request.getHeader(AppConst.HEADER_USERNAME), param.getRoleName(), param.getChangedItems());
        Long roleId = param.getRoleId();
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));
        Role role = roleMapperService.findRoleByIdAndTenantId(roleId, tenantId);
        Assert.notNull(role, "参数错误");

        Set<RoleActionItem> changedItems = param.getChangedItems();
        changedItems.stream().forEach(e -> {
            String key = e.getKey(); // menu菜单key
            Set<String> actions = e.getActions(); // [] || ["user:add","user:list"] || ["list"] || ["user:add,user:list"]
            String ids = "";
            if (actions.size() == 0) {
                ids = "-1";
            } else if (actions.size() == 1 && "list".equals(actions.iterator().next())) {
                ids = "0";
            } else {
                Set<String> actiosFields = actions.stream().flatMap(i -> Splitter.on(",").trimResults().omitEmptyStrings().splitToStream(i)).distinct().collect(Collectors.toSet());
                List<Action> acs = actionMapperService.getActionsByNames(actiosFields);
                ids = Joiner.on(",").skipNulls().join(acs.stream().map(o -> o.getId()).collect(Collectors.toSet()));
            }

            // 更新 或 新增 role_menu_actions
            RoleMenuAction rmaItem = roleMenuActionMapper.findByRoleIdAndMenuKey(roleId, key);
            if (ObjectUtils.isEmpty(rmaItem)) {
                roleMenuActionMapper.addRoleMenuActionItem(roleId, key, ids);
            } else {
                rmaItem.setActionIds(ids);
                roleMenuActionMapper.updateById(rmaItem);
            }
        });
    }

    @Override
    public PageOut<List<ActionOut>> getActionList(ActionQueryIn param) {
        Page<Action> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<Action> actions = actionMapper.getActions(page, param.getApi());
        PageOut<List<ActionOut>> out = new PageOut<>();

        out.setTotal(actions.getTotal());
        out.setRows(actions.getRecords().stream().map(e -> new ActionOut(e.getId(), e.getApi(), e.getAction())).collect(Collectors.toList()));
        return out;
    }

    @Override
    public void addOrUpdateAction(ActionIn param) {
        Action exist = actionMapper.selectOne(new QueryWrapper<Action>().lambda().eq(Action::getApi, param.getApi()));

        if (!ObjectUtils.isEmpty(param.getId()) && param.getId().compareTo(0L) > 0) {
            // update
            Action action = actionMapper.selectById(param.getId());
            if (!ObjectUtils.isEmpty(exist) && !exist.getId().equals(action.getId())) {
                throw new AppException(RCode.PARAM_ERROR.getCode(), "api已存在");
            }
            action.setApi(param.getApi());
            action.setAction(param.getAction());
            actionMapper.updateById(action);
        } else {
            // add
            Assert.isNull(exist, "api已存在");
            actionMapper.insert(new Action(null, param.getApi(), param.getAction()));
        }
    }

    @Override
    public void deleteAction(ActionIn param) {
        actionMapper.delete(new QueryWrapper<Action>().lambda().eq(Action::getId, param.getId()));
    }

    private ActionItem getAction(Long roleId, Menu menu) {
        ActionItem item = new ActionItem();
        item.setTitle(menu.getTitle());
        item.setKey(menu.getKey());
        List<OptionItem> options;

        // actions权限
        String actions = menu.getActions();
        options = JSON.parseArray(actions, OptionItem.class);

        item.setOptions(options);

        // 此角色 当前menu拥有的action权限
        RoleMenuAction action = roleMenuActionMapper.selectOne(new QueryWrapper<RoleMenuAction>().lambda().eq(RoleMenuAction::getRoleId, roleId).eq(RoleMenuAction::getMenuId, menu.getId()));
        if (!ObjectUtils.isEmpty(action)) {
            if ("0".equals(action.getActionIds())) {
                item.setChecked(Collections.singleton("list"));
            } else if (!"-1".equals(action.getActionIds())) {
                List<Action> actionList = actionMapperService.getActions(Splitter.on(",").trimResults().omitEmptyStrings()
                        .splitToStream(action.getActionIds()).mapToLong(Long::parseLong).boxed().collect(Collectors.toSet()));
                // 当前角色--菜单下实际拥有的action 字段集合
                Set<String> roleAcs = actionList.stream().map(e -> e.getAction()).collect(Collectors.toSet());
                Set<String> checked = new HashSet<>();

                options.stream().forEach(e -> {
                    // 当前菜单具有的所有action 字段名
                    Set<String> acs = Splitter.on(",").trimResults().omitEmptyStrings().splitToStream(e.getValue()).collect(Collectors.toSet());
                    // 当前角色有的 ['user:add','user:update'] --- ['user:add']
                    if (roleAcs.containsAll(acs)) {
                        checked.add(e.getValue());
                    }
                });
                item.setChecked(checked);
            }
        }

        return item;
    }

}
