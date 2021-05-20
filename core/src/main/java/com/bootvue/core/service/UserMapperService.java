package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.ddo.user.UserDo;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapperService {
    private final UserMapper userMapper;

    /**
     * 用户id 查询用户
     *
     * @param id user id
     * @return user
     */
    @Cacheable(cacheNames = AppConst.USER_CACHE, key = "#id", unless = "#result == null")
    public User findById(Long id) {
        return userMapper.selectOne(new QueryWrapper<User>().lambda()
                .eq(User::getId, id).eq(User::getStatus, true)
                .isNull(User::getDeleteTime));
    }

    public User findByUsernameAndPassword(String username, String password, String tenantCode) {
        return userMapper.findByUsernameAndPassword(username, password, tenantCode);
    }

    /**
     * 手机号查询user
     *
     * @param phone      手机号
     * @param tenantCode 租户编号
     * @return user
     */
    public User findByPhone(String phone, String tenantCode) {
        return userMapper.findByPhone(phone, tenantCode);
    }


    /**
     * openid查询user  普通用户角色
     *
     * @param openid     openid
     * @param tenantCode 租户编号
     * @return user
     */
    public User findByOpenid(String openid, String tenantCode) {
        return userMapper.findByOpenid(openid, tenantCode);
    }

    public IPage<UserDo> listUsers(Page<User> page, Long tenantId, String username) {
        return userMapper.listUsers(page, username, tenantId);
    }

    // 管理员用户 role_id置为 0
    public void removeUserRoleId(Long roleId, Long tenantId) {
        userMapper.removeRoleId(roleId, tenantId);
    }
}
