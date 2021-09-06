package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.entity.Admin;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper extends BaseMapper<Admin> {

    IPage<Admin> listAdmin(Page<Admin> page, @Param("username") String username, @Param("phone") String phone);

    Admin findByUserNameOrPhone(@Param("username") String username, @Param("phone") String phone);
}