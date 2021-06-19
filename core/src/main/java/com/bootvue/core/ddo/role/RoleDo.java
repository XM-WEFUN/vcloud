package com.bootvue.core.ddo.role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDo {
    private Long id;
    private String roleName;
    private String tenantName;
}
