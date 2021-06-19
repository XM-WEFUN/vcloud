package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.ddo.admin.AdminDo;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminMapperService {
    private final AdminMapper adminMapper;

    /**
     * 用户id 查询管理员用户 (有效的)
     *
     * @param id admin id
     * @return admin
     */
    @Cacheable(cacheNames = AppConst.ADMIN_CACHE, key = "#id", unless = "#result == null")
    public Admin findById(Long id) {
        return adminMapper.selectOne(new QueryWrapper<Admin>().lambda()
                .eq(Admin::getId, id).eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime));
    }

    public Admin findByUsernameAndPassword(String username, String password, String tenantCode) {
        return adminMapper.findByUsernameAndPassword(username, password, tenantCode);
    }

    /**
     * 手机号查询admin
     *
     * @param phone      手机号
     * @param tenantCode 租户编号
     * @return admin
     */
    public Admin findByPhone(String phone, String tenantCode) {
        return adminMapper.findByPhone(phone, tenantCode);
    }

    public Admin findByPhoneAndTenantId(String phone, Long tenantId) {
        return adminMapper.selectOne(new QueryWrapper<Admin>().lambda()
                .eq(Admin::getPhone, phone).eq(Admin::getTenantId, tenantId)
        );
    }

    public IPage<AdminDo> listAdmins(Page<Admin> page, Long tenantId, String username) {
        return adminMapper.listAdmins(page, username, tenantId);
    }

    // 管理员用户 role_id置为 0
    public void removeUserRoleId(Long roleId, Long tenantId) {
        adminMapper.removeRoleId(roleId, tenantId);
    }

    public Set<Long> listAdminsByRoleName(Long tenantId, String roleName) {
        return adminMapper.listAdminsByRoleName(tenantId, roleName);
    }

    @CacheEvict(cacheNames = AppConst.ADMIN_CACHE, allEntries = true)
    public void updateAdminRoles(Set<Long> selectedKeys, Set<Long> unSelectedKeys, Long roleId, Long tenantId) {
        if (!CollectionUtils.isEmpty(selectedKeys)) {
            adminMapper.batchUpdateRole(selectedKeys, roleId, tenantId);
        }

        if (!CollectionUtils.isEmpty(unSelectedKeys)) {
            adminMapper.batchCancelRole(unSelectedKeys, roleId, tenantId);
        }
    }
}
