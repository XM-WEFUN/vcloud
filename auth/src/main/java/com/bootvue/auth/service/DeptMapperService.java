package com.bootvue.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.DeptMapper;
import com.bootvue.datasource.entity.Dept;
import org.springframework.stereotype.Service;

@Service
public class DeptMapperService extends ServiceImpl<DeptMapper, Dept> implements IService<Dept> {
}
