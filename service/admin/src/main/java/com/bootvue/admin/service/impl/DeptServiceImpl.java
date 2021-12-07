package com.bootvue.admin.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.DeptItem;
import com.bootvue.admin.service.DeptMapperService;
import com.bootvue.admin.service.DeptService;
import com.bootvue.admin.service.UserDeptMapperService;
import com.bootvue.admin.service.UserMapperService;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.model.AppUser;
import com.bootvue.datasource.entity.Dept;
import com.bootvue.datasource.entity.User;
import com.bootvue.datasource.entity.UserDept;
import com.bootvue.datasource.type.DeptTypeEnum;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeptServiceImpl implements DeptService {

    private final DeptMapperService deptMapperService;
    private final UserMapperService userMapperService;
    private final UserDeptMapperService userDeptMapperService;

    @Override
    public List<Tree<String>> list(Long tenantId, AppUser user) {
        Assert.isTrue(AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) || user.getTenantId().equals(tenantId), "参数错误");

        List<DeptItem> depts = deptMapperService.listByTenantId(tenantId);

        // 树节点
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setWeightKey("sort");
        treeNodeConfig.setIdKey("id");
        treeNodeConfig.setParentIdKey("pid");

        //转换器
        return TreeUtil.build(depts, "0", treeNodeConfig, (node, tree) -> {
            tree.setId(String.valueOf(node.getId()));
            tree.setParentId(String.valueOf(node.getPid()));
            tree.setWeight(node.getSort());
            tree.setName(node.getName());

            tree.putExtra("pname", node.getPname());
            tree.putExtra("type", node.getType());
            tree.putExtra("tenant_id", String.valueOf(node.getTenantId()));
            tree.putExtra("tenant_name", node.getTenantName());
            tree.putExtra("contact_name", node.getContactName());
            tree.putExtra("contact_phone", node.getContactPhone());
            tree.putExtra("remark", node.getRemark());
        });
    }

    @Override
    public void delete(Long id, AppUser user) {
        Dept dept = deptMapperService.getById(id);
        Assert.notNull(dept, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Assert.isTrue(user.getTenantId().equals(dept.getTenantId()), "参数错误");
        }
        Set<Long> ids = new HashSet<>();
        ids.add(id);
        // 删除dept 以及子级
        ids.addAll(deptMapperService.list(new QueryWrapper<>(new Dept().setPId(id))).stream().map(Dept::getId).collect(Collectors.toSet()));

        deptMapperService.removeByIds(ids);

        // user dept
        userDeptMapperService.remove(new QueryWrapper<UserDept>().lambda().in(UserDept::getDeptId, ids));
    }

    @Override
    public void addOrUpdate(DeptItem param, AppUser user) {
        Dept dept;
        if (ObjectUtils.isEmpty(param.getId())) {
            // 新增
            Assert.isTrue(AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) || param.getTenantId().equals(user.getTenantId()), "参数错误");
            dept = new Dept();
            dept.setTenantId(param.getTenantId());
        } else {
            // 更新
            dept = deptMapperService.getById(param.getId());
        }
        dept.setName(StringUtils.hasText(param.getName()) ? param.getName() : dept.getName());
        dept.setType(DeptTypeEnum.find(param.getType()));
        dept.setSort(ObjectUtils.isEmpty(param.getSort()) ? 0 : param.getSort());
        dept.setPId(ObjectUtils.isEmpty(param.getPid()) ? 0L : param.getPid());
        dept.setContactName(StringUtils.hasText(param.getContactName()) ? param.getContactName() : "");
        dept.setContactPhone(StringUtils.hasText(param.getContactPhone()) ? param.getContactPhone() : "");
        dept.setRemark(StringUtils.hasText(param.getRemark()) ? param.getRemark() : "");

        deptMapperService.saveOrUpdate(dept);
    }

    @Override
    public void assignUser(AssignIn param, AppUser user) {
        // 验证部门
        Dept dept = deptMapperService.getById(param.getId());
        Assert.notNull(dept, "参数错误");

        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Assert.isTrue(dept.getTenantId().equals(user.getTenantId()), "参数错误");
        }
        // 分配的用户id
        Set<Long> ids = param.getIds();

        log.info("部门: {} 分配用户: {}", dept.getName(), ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 清除
            userDeptMapperService.remove(new QueryWrapper<>(new UserDept().setDeptId(param.getId())));
            return;
        }

        // 验证 ids是否都属于 此租户
        List<User> users = userMapperService.list(new QueryWrapper<User>().lambda().in(User::getId, ids));
        Set<Long> tenants = users.stream().map(User::getTenantId).collect(Collectors.toSet());
        Assert.isTrue(tenants.size() == 1 && tenants.contains(dept.getTenantId()), "参数错误");

        // 此部门已有 user id
        Set<Long> orignIds = userDeptMapperService.list(new QueryWrapper<>(new UserDept().setDeptId(param.getId()))).stream().map(UserDept::getUserId).collect(Collectors.toSet());

        // 需要删除的 id
        Sets.SetView<Long> removeIds = Sets.difference(orignIds, ids);
        // 需要新增的 id
        Sets.SetView<Long> addIds = Sets.difference(ids, orignIds);

        if (!CollectionUtils.isEmpty(removeIds)) {
            userDeptMapperService.remove(new QueryWrapper<UserDept>().lambda().eq(UserDept::getDeptId, param.getId()).in(UserDept::getUserId, removeIds));
        }

        if (!CollectionUtils.isEmpty(addIds)) {
            userDeptMapperService.saveBatch(addIds.stream().map(e -> new UserDept(null, e, param.getId())).collect(Collectors.toList()));
        }
    }

    @Override
    public Set<String> listByUser(Long userId, AppUser u) {
        User user = userMapperService.getById(userId);
        Assert.notNull(user, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(u.getTenantId())) {
            Assert.isTrue(u.getTenantId().equals(user.getTenantId()), "参数错误");
        }

        List<UserDept> depts = userDeptMapperService.list(new QueryWrapper<>(new UserDept().setUserId(userId)));
        return depts.stream().map(e -> String.valueOf(e.getDeptId())).collect(Collectors.toSet());
    }

}
