package com.bootvue.web.aop;

import cn.hutool.core.annotation.AnnotationUtil;
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
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class AuthAspect {

    @Around("@annotation(com.bootvue.web.annotation.PreAuth) || @within(com.bootvue.web.annotation.PreAuth)")
    public Object preAuth(ProceedingJoinPoint point) throws Throwable {
        //获取request header
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String roles = request.getHeader("roles");

        // 与PreAuth注解指定的value对比
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());

        // 方法上的  方法上的roles优先级高
        PreAuth preAuth = null;
        preAuth = AnnotationUtil.getAnnotation(method, PreAuth.class);
        if (ObjectUtils.isEmpty(preAuth)) {
            // 类上的
            preAuth = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), PreAuth.class);
        }

        if (!ObjectUtils.isEmpty(preAuth) && !StringUtils.isEmpty(preAuth.value())
                && !hasAuthorization(roles, preAuth.value())) {
            log.error("用户: {} id: {} , roles: {}, 请求资源: {} -- {}  权限不足",
                    request.getHeader("username"),
                    request.getHeader("user_id"),
                    request.getHeader("roles"),
                    request.getRequestURI(), preAuth.value()
            );
            throw new AppException(RCode.ACCESS_DENY);
        }

        return point.proceed();
    }

    /**
     * 检查用户是否有需要的权限
     *
     * @param roles  用户权限
     * @param values 需要的权限
     * @return boolean
     */
    private boolean hasAuthorization(String roles, String values) {
        boolean flag = false;
        if (!StringUtils.isEmpty(roles)) {
            if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(roles, values)) { // 正好相等
                flag = true;
            } else {
                // 逗号分割比较
                // 目标需要的权限集合
                Set<String> vs = Arrays.stream(values.split(",")).map(String::trim).collect(Collectors.toSet());

                // 已有的权限集合
                Set<String> rs = Arrays.stream(roles.split(",")).map(String::trim).collect(Collectors.toSet());
                if (rs.containsAll(vs)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

}
