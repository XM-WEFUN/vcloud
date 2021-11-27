package com.bootvue.admin.service.impl;

import com.bootvue.admin.dto.TenantIn;
import com.bootvue.admin.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TenantServiceImpl implements TenantService {

    @Override
    public void add(TenantIn param) {

    }
}
