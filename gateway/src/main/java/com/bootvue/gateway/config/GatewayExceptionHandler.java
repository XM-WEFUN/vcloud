package com.bootvue.gateway.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * gateway网关异常拦截处理
 */
@Slf4j
public class GatewayExceptionHandler extends DefaultErrorWebExceptionHandler {
    private final Map<String, Object> response = new HashMap<>(2);

    /**
     * Create a new {@code DefaultErrorWebExceptionHandler} instance.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param errorProperties    the error configuration properties
     * @param applicationContext the current application context
     * @since 2.4.0
     */
    public GatewayExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }


    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        response.clear();
        if (error instanceof AppException) {
            AppException exception = (AppException) error;
            response.put("code", exception.getCode());
            response.put("msg", exception.getMsg());
        } else if (error instanceof ResponseStatusException) {
            response.put("code", RCode.NOT_FOUND.getCode());
            response.put("msg", "Service Not Found");
        } else if (BlockException.isBlockException(error)) {
            BlockException exception = (BlockException) error;
            // sentinel 拦截
            response.put("code", RCode.GATEWAY_ERROR.getCode());
            response.put("msg", "当前请求受限");
        } else {
            response.put("code", RCode.DEFAULT.getCode());
            response.put("msg", RCode.DEFAULT.getMsg());
            log.error("gateway拦截到异常: {} , path: {}", error.getMessage(), request.path());
        }
        return response;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return 200;
    }

}
