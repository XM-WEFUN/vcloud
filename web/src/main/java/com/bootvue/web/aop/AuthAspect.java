package com.bootvue.web.aop;

import cn.hutool.core.annotation.AnnotationUtil;
import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import com.bootvue.datasource.type.AccountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class AuthAspect {

    @Around("@annotation(com.bootvue.common.annotation.PreAuth) || @within(com.bootvue.common.annotation.PreAuth)")
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
        if (preAuth.superOnly()) {
            handleSuperOnlyValid(preAuth, request);
        }

        //todo 校验用户 是否拥有这些role
        List<String> roles = Arrays.asList(preAuth.hasRole());
        log.info("此接口需要角色: {}", roles);

        //todo 校验用户 是否拥有这些permission

        return point.proceed();
    }

    // 某些接口只能平台管理员访问
    private void handleSuperOnlyValid(PreAuth preAuth, HttpServletRequest request) {

        String tenantId = request.getParameter("tenantId");
        Integer type = Integer.valueOf(request.getParameter("type"));

        if (!ObjectUtils.isEmpty(preAuth) && !AppConst.ADMIN_TENANT_ID.equals(Long.valueOf(tenantId)) && !AccountTypeEnum.ADMIN.getValue().equals(type)) {
            log.error("用户: {} id: {} 请求资源: {} 无权访问", request.getParameter("account"), request.getParameter("id"), request.getRequestURI());
            throw new AppException(RCode.ACCESS_DENY);
        }
    }
}
