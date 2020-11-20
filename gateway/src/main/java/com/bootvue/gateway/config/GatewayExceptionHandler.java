package com.bootvue.gateway.config;

import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GatewayExceptionHandler extends DefaultErrorWebExceptionHandler {
    private Map<String, Object> response = new HashMap<>();

    /**
     * Create a new {@code DefaultErrorWebExceptionHandler} instance.
     *
     * @param errorAttributes    the error attributes
     * @param resourceProperties the resources configuration properties
     * @param errorProperties    the error configuration properties
     * @param applicationContext the current application context
     */
    public GatewayExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }


    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        response.clear();
        if (error instanceof AppException) {
            AppException exception = (AppException) error;
            response.put("code", exception.getCode());
            response.put("msg", exception.getMsg());
            log.error("gateway系统异常: {} , path: {}", exception.getMsg(), request.path());
        } else if (error instanceof ResponseStatusException) {
            ResponseStatusException ex = (ResponseStatusException) error;
            response.put("code", 404);
            response.put("msg", "Service Not Found");
            log.error("gateway系统异常: {} , path: {}", ex.getStatus(), request.path());
        } else {
            response.put("code", RCode.DEFAULT.getCode());
            response.put("msg", RCode.DEFAULT.getMsg());
            log.error("gateway系统异常: {} , path: {}", error.getMessage(), request.path());
        }
        return response;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return 503;
    }

}
