package com.bootvue.core.handler;

import com.bootvue.common.result.AppException;
import com.bootvue.common.result.R;
import com.bootvue.common.result.RCode;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {
    @ExceptionHandler(value = AppException.class)
    @ResponseBody
    public <T> R<T> handleException(AppException e) {
        return new R<>(e.getCode(), e.getMsg(), null);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    public <T> R<T> handleException(Exception e) {
        log.error("拦截到异常: ", e);
        return new R<>(RCode.DEFAULT.getCode(), StringUtils.isEmpty(e.getMessage()) ? RCode.DEFAULT.getMsg() : e.getMessage(), null);
    }

}
