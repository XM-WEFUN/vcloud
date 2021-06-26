package com.bootvue.admin.controller.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TenantQueryIn {
    private Long current = 1L;
    private Long pageSize = 10L;
    private String tenantName;
}
