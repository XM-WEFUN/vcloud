package com.bootvue.core.service;

import com.bootvue.core.mapper.RoleAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleAdminMapperService {
    private final RoleAdminMapper roleAdminMapper;
}
