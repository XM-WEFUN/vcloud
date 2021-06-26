package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.entity.Action;
import org.apache.ibatis.annotations.Param;

public interface ActionMapper extends BaseMapper<Action> {
    IPage<Action> getActions(Page<Action> page, @Param("api") String api);
}