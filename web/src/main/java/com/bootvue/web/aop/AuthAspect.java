package com.bootvue.web.aop;

import cn.hutool.core.annotation.AnnotationUtil;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.web.annotation.PreAuth;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class AuthAspect {

    @Around("@annotation(com.bootvue.web.annotation.PreAuth) || @within(com.bootvue.web.annotation.PreAuth)")
    public Object preAuth(ProceedingJoinPoint point) throws Throwable {
        //获取request 参数----> 来自gateway
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 与PreAuth注解指定的value对比
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());

        // 方法上的  方法上的注解优先级高
        PreAuth preAuth = null;
        preAuth = AnnotationUtil.getAnnotation(method, PreAuth.class);
        if (ObjectUtils.isEmpty(preAuth)) {
            // 类上的
            preAuth = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), PreAuth.class);
        }

        // 是否只允许  运营平台管理员访问
        handleSuperOnlyValid(preAuth, request);

        return point.proceed();
    }

    private void handleSuperOnlyValid(PreAuth preAuth, HttpServletRequest request) {

        String tenantId = request.getParameter("tenantId");

        if (!ObjectUtils.isEmpty(preAuth) && preAuth.superOnly() && !AppConst.ADMIN_TENANT_ID.equals(Long.valueOf(tenantId))) {
            log.error("用户: {} id: {} 请求资源: {} 无权访问", request.getParameter("username"), request.getParameter("id"), request.getRequestURI());
            throw new AppException(RCode.ACCESS_DENY);
        }
    }
}
