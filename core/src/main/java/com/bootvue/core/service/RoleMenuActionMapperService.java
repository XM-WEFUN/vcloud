package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.RoleMenuAction;
import com.bootvue.core.mapper.RoleMenuActionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleMenuActionMapperService {
    private final RoleMenuActionMapper roleMenuActionMapper;

    @Cacheable(cacheNames = AppConst.ACTION_CACHE, key = "#roleId", unless = "#result == null || #result.size() == 0")
    public List<RoleMenuAction> getRoleMenuActions(Long roleId) {
        return roleMenuActionMapper.selectList(
                new QueryWrapper<RoleMenuAction>().lambda()
                        .eq(RoleMenuAction::getRoleId, roleId)
        );
    }

    // 删除某个role_id对应的所有记录
    public void delByRoleId(Long id) {
        roleMenuActionMapper.delByRoleId(id);
    }
}
