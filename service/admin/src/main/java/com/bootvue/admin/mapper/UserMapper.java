package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.UserItem;
import com.bootvue.datasource.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {
    Page<UserItem> users(Page<UserItem> page, @Param("tenantId") Long tenantId, @Param("account") String account, @Param("phone") String phone);
}
