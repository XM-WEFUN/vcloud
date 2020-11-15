package com.bootvue.core.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    private Integer code;
    private String msg;

    public AppException(RCode rCode) {
        this.code = rCode.getCode();
        this.msg = rCode.getMsg();
    }
}
