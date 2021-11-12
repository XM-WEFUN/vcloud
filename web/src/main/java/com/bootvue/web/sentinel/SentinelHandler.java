package com.bootvue.web.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sentinel 异常处理
 */
@Configuration
@Slf4j
public class SentinelHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        // 可以按BlockException类型分别处理
        throw new AppException(RCode.GATEWAY_ERROR.getCode(), "当前请求受限");
    }
}
