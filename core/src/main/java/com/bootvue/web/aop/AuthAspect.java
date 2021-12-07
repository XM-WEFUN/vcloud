package com.bootvue.web.aop;

import cn.hutool.core.annotation.AnnotationUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import com.bootvue.datasource.entity.Menu;
import com.bootvue.datasource.entity.Role;
import com.bootvue.datasource.entity.RoleMenu;
import com.bootvue.datasource.entity.UserRole;
import com.bootvue.datasource.type.AccountTypeEnum;
import com.bootvue.web.mapper.AopMenuMapper;
import com.bootvue.web.mapper.AopRoleMapper;
import com.bootvue.web.mapper.AopRoleMenuMapper;
import com.bootvue.web.mapper.AopUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAspect {

    private final AopRoleMapper aopRoleMapper;
    private final AopMenuMapper aopMenuMapper;
    private final AopUserRoleMapper aopUserRoleMapper;
    private final AopRoleMenuMapper aopRoleMenuMapper;

    @Around("@annotation(com.bootvue.common.annotation.PreAuth) || @within(com.bootvue.common.annotation.PreAuth)")
    public Object preAuth(ProceedingJoinPoint point) throws Throwable {
        //获取request 参数----> 来自gateway
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 与PreAuth注解指定的value对比
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());

        // 方法上的  方法上的注解优先级高
        PreAuth preAuthMethod = AnnotationUtil.getAnnotation(method, PreAuth.class);
        PreAuth preAuthClass = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), PreAuth.class);

        boolean superOnly = ObjectUtils.isEmpty(preAuthMethod) ? preAuthClass.superOnly() : preAuthMethod.superOnly();

        // 需要继承类上的参数值
        String[] roles = ObjectUtils.isEmpty(preAuthMethod) ? preAuthClass.hasRole() : ArrayUtils.addAll(preAuthMethod.hasRole(), ObjectUtils.isEmpty(preAuthClass.getClass()) ? null : preAuthClass.hasRole());
        String[] perms = ObjectUtils.isEmpty(preAuthMethod) ? preAuthClass.hasPermission() : ArrayUtils.addAll(preAuthMethod.hasPermission(), ObjectUtils.isEmpty(preAuthClass.getClass()) ? null : preAuthClass.hasPermission());


        // 是否只允许  运营平台管理员访问
        if (superOnly) {
            handleSuperOnlyValid(request);
        }

        Set<String> rs = Arrays.stream(roles).filter(StringUtils::hasText).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(rs)) {
            handleRolesValid(rs, request);
        }

        Set<String> ps = Arrays.stream(perms).filter(StringUtils::hasText).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(ps)) {
            handlePermsValid(ps, request);
        }

        return point.proceed();
    }

    // 验证菜单/按钮权限
    private void handlePermsValid(Set<String> perms, HttpServletRequest request) {
        log.info("此接口需要Permisson权限字段: {}", perms);
        // 用户角色拥有的menu action
        List<UserRole> userRoles = aopUserRoleMapper.selectList(new QueryWrapper<>(new UserRole().setUserId(Long.valueOf(request.getParameter("id")))));
        List<RoleMenu> roleMenus = aopRoleMenuMapper.selectList(new QueryWrapper<RoleMenu>().lambda().in(RoleMenu::getRoleId, userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet())));
        List<Menu> menus = aopMenuMapper.selectList(new QueryWrapper<Menu>().lambda().in(Menu::getId, roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toSet())));
        if (!menus.stream().map(Menu::getAction).collect(Collectors.toSet()).containsAll(perms)) {
            throw new AppException(RCode.ACCESS_DENY);
        }
    }

    // 验证角色权限
    private void handleRolesValid(Set<String> roles, HttpServletRequest request) {
        log.info("此接口需要角色权限字段: {}", roles);
        // 用户拥有的role action
        List<UserRole> userRoles = aopUserRoleMapper.selectList(new QueryWrapper<>(new UserRole().setUserId(Long.valueOf(request.getParameter("id")))));
        List<Role> rs = aopRoleMapper.selectList(new QueryWrapper<Role>().lambda().in(Role::getId, userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet())));
        if (!rs.stream().map(Role::getAction).collect(Collectors.toSet()).containsAll(roles)) {
            throw new AppException(RCode.ACCESS_DENY);
        }
    }


    // 某些接口只能平台管理员访问
    private void handleSuperOnlyValid(HttpServletRequest request) {

        String tenantId = request.getParameter("tenantId");
        Integer type = Integer.valueOf(request.getParameter("type"));

        if (!AppConst.ADMIN_TENANT_ID.equals(Long.valueOf(tenantId)) && !AccountTypeEnum.ADMIN.getValue().equals(type)) {
            log.error("用户: {} id: {} 请求资源: {} 无权访问", request.getParameter("account"), request.getParameter("id"), request.getRequestURI());
            throw new AppException(RCode.ACCESS_DENY);
        }
    }
}
