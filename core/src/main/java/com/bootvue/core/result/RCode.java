package com.bootvue.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RCode {
    SUCCESS(200, "success"),
    PARAM_ERROR(400, "Bad Request"),
    UNAUTHORIZED_ERROR(401, "Unauthorized"),
    ACCESS_DENY(403, "Forbidden"),
    DEFAULT(600, "系统异常"),
    ;

    private final Integer code;
    private final String msg;
}
