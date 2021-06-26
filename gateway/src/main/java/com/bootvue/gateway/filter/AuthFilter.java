package com.bootvue.gateway.filter;

import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.config.app.Key;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.PlatformType;
import com.bootvue.core.entity.Action;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.entity.RoleMenuAction;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.ActionMapperService;
import com.bootvue.core.service.AdminMapperService;
import com.bootvue.core.service.RoleMenuActionMapperService;
import com.bootvue.core.util.JwtUtil;
import com.google.common.base.Splitter;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AppConfig appConfig;
    private final AdminMapperService adminMapperService;
    private final ActionMapperService actionMapperService;
    private final RoleMenuActionMapperService roleMenuActionMapperService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (!CollectionUtils.isEmpty(appConfig.getSkipUrls())) {
            for (String skipUrl : appConfig.getSkipUrls()) {
                if (PATH_MATCHER.match(skipUrl, path)) {
                    // /auth/oauth/**下的所有接口需要验证客户端key
                    if (path.startsWith("/auth/oauth")) {
                        // 检查客户端Key
                        Key keys = appConfig.getKeys().stream()
                                .filter(it -> it.getAppid().equals(request.getQueryParams().getFirst("appid"))
                                        && it.getSecret().equals(request.getQueryParams().getFirst("secret")))
                                .findAny().orElse(null);

                        if (ObjectUtils.isEmpty(keys)) {
                            throw new AppException(RCode.PARAM_ERROR.getCode(), "客户端参数无效");
                        }
                    }
                    return chain.filter(exchange);
                }
            }
        }

        // access_token校验

        // ServerHttpResponse resp = exchange.getResponse();
        String token = exchange.getRequest().getHeaders().getFirst("token");
        Claims claims;

        if (!StringUtils.hasText(token) || !JwtUtil.isVerify(token) || ObjectUtils.isEmpty(claims = JwtUtil.decode(token))
                || !AppConst.ACCESS_TOKEN.equalsIgnoreCase(claims.get("type", String.class))) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        PlatformType platform = PlatformType.getPlatform(claims.get(AppConst.HEADER_PLATFORM, Integer.class)); // 账号所属平台

        switch (platform) {
            case ADMIN:
                // 数据库再次校验用户信息 (cache)
                Admin admin = adminMapperService.findById(claims.get(AppConst.HEADER_USER_ID, Long.class));

                if (ObjectUtils.isEmpty(admin)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }

                // api接口权限校验   appconfig相关配置
                try {
                    handleRequestAuthorization(path, admin);
                } catch (AppException e) {
                    throw new AppException(e.getCode(), e.getMsg());
                } catch (Exception e) {
                    throw new AppException(RCode.DEFAULT.getCode(), RCode.DEFAULT.getMsg());
                }

                return chain.filter(exchange.mutate().request(handleRequest(request, platform, admin.getId(), admin.getRoleId(), admin.getTenantId(), "", admin.getUsername())).build());
            default:
                // 用户平台
                // 再次校验用户信息
                log.info("用户再次校验........");
                return null;
        }
    }

    private ServerHttpRequest handleRequest(ServerHttpRequest request, PlatformType platform, Long id, Long roleId, Long tenantId, String openid, String username) {
        // request header添加用户信息
        // request header添加上用户信息  中文需要URIEncode

        return request.mutate().headers((headers) -> {
            headers.add(AppConst.HEADER_USER_ID, String.valueOf(id));
            headers.add(AppConst.HEADER_OPENID, openid);
            headers.add(AppConst.HEADER_ROLEID, String.valueOf(roleId));
            headers.add(AppConst.HEADER_USERNAME, String.valueOf(URLEncoder.encode(username, StandardCharsets.UTF_8)));
            headers.add(AppConst.HEADER_TENANT_ID, String.valueOf(tenantId));
            headers.add(AppConst.HEADER_PLATFORM, String.valueOf(platform.getValue()));
        }).build();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    // 管理员用户权限验证
    private void handleRequestAuthorization(String path, Admin admin) {
        if (CollectionUtils.isEmpty(appConfig.getAuthorizationUrls())) {
            return;
        }
        // 不需要校验
        if (!CollectionUtils.isEmpty(appConfig.getUnAuthorizationUrls())) {
            for (String unAuthorizationUrl : appConfig.getUnAuthorizationUrls()) {
                if (PATH_MATCHER.match(unAuthorizationUrl, path)) {
                    return;
                }
            }
        }

        // 需要校验权限的请求
        boolean flag = true;
        for (String authorizationUrl : appConfig.getAuthorizationUrls()) {
            if (PATH_MATCHER.match(authorizationUrl, path)) {
                // 请求接口需要的权限
                Action action = actionMapperService.getAction(path);
                // 用户角色对应的action权限
                List<RoleMenuAction> actions = roleMenuActionMapperService.getRoleMenuActions(admin.getRoleId());

                if (ObjectUtils.isEmpty(action) || CollectionUtils.isEmpty(actions)) {
                    flag = false;
                    break;
                }
                List<String> ids = actions.stream().flatMap(e ->
                        Splitter.on(",").trimResults().omitEmptyStrings().splitToStream(e.getActionIds()))
                        .collect(Collectors.toList());
                if (!ids.contains(String.valueOf(action.getId()))) {
                    flag = false;
                }
                break;
            }
        }
        if (!flag) {
            log.warn("用户: {} id: {} 角色id: {} 访问资源: {} 没有权限 ", admin.getUsername(), admin.getId(), admin.getRoleId(), path);
            throw new AppException(RCode.ACCESS_DENY);
        }
    }
}
