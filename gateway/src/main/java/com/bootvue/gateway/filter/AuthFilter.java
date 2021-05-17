package com.bootvue.gateway.filter;

import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.config.app.Keys;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.Action;
import com.bootvue.core.entity.RoleMenuAction;
import com.bootvue.core.entity.User;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.ActionMapperService;
import com.bootvue.core.service.RoleMenuActionMapperService;
import com.bootvue.core.service.UserMapperService;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AppConfig appConfig;
    private final UserMapperService userMapperService;
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
                        Keys keys = appConfig.getAuthKey().stream()
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
        Claims claims = null;

        if (!StringUtils.hasText(token) || !JwtUtil.isVerify(token) || ObjectUtils.isEmpty(claims = JwtUtil.decode(token))
                || !AppConst.ACCESS_TOKEN.equalsIgnoreCase(claims.get("type", String.class))) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        // 数据库再次校验用户信息 (cache)
        User user = userMapperService.findById(claims.get("user_id", Long.class));

        if (ObjectUtils.isEmpty(user)) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        // api接口权限校验 /admin /xxx的需要权限校验
        try {
            handleRequestAuthorization(path, user);
        } catch (AppException e) {
            throw new AppException(e.getCode(), e.getMsg());
        } catch (Exception e) {
            throw new AppException(RCode.DEFAULT.getCode(), RCode.DEFAULT.getMsg());
        }

        // request header添加用户信息
        // request header添加上用户信息  中文需要URIEncode

        request.mutate().headers((headers) -> {
            headers.add("user_id", String.valueOf(user.getId()));
            headers.add("username", user.getUsername());
            headers.add("openid", user.getOpenid());
            headers.add("role_id", String.valueOf(user.getRoleId()));
            headers.add("tenant_id", String.valueOf(user.getTenantId()));
        }).build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    // 权限验证
    private void handleRequestAuthorization(String path, User user) {
        if (CollectionUtils.isEmpty(appConfig.getAuthorizationUrls())) {
            return;
        }

        boolean flag = true;
        for (String authorizationUrl : appConfig.getAuthorizationUrls()) {
            if (PATH_MATCHER.match(authorizationUrl, path)) {
                Action action = actionMapperService.getAction(path);
                // 用户角色对应的action权限
                List<RoleMenuAction> actions = roleMenuActionMapperService.getRoleMenuActions(user.getRoleId());

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
            log.warn("用户: {} id: {} 角色id: {} 访问资源: {} 没有权限 ", user.getUsername(), user.getId(), user.getRoleId(), path);
            throw new AppException(RCode.ACCESS_DENY);
        }
    }
}
