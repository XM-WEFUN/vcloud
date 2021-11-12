package com.bootvue.gateway.filter;

import com.bootvue.common.config.app.AppConfig;
import com.bootvue.common.config.app.Key;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.constant.TokenLabelEnum;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import com.bootvue.common.util.JwtUtil;
import com.bootvue.gateway.service.AuthService;
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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AppConfig appConfig;
    private final AuthService authService;

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
                        Key key = appConfig.getKeys().stream()
                                .filter(it -> it.getAppid().equals(request.getQueryParams().getFirst("appid"))
                                        && it.getSecret().equals(request.getQueryParams().getFirst("secret"))
                                        && String.valueOf(it.getPlatform()).equals(request.getQueryParams().getFirst("platform"))
                                )
                                .findAny().orElse(null);

                        if (ObjectUtils.isEmpty(key)) {
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
                || !AppConst.ACCESS_TOKEN.equalsIgnoreCase(claims.get(AppConst.TOKEN_TYPE, String.class))) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        TokenLabelEnum label = TokenLabelEnum.valueOf(claims.get(AppConst.TOKEN_LABEL, String.class)); // 账号类型
        Long userId = claims.get(AppConst.TOKEN_USER_ID, Long.class);
        Long tenantId = claims.get(AppConst.TOKEN_TENANT_ID, Long.class);

        handleRequestPathValid(path, label);  // 接口验证

        switch (label) {
            case ADMIN:
                // 再次校验用户信息 (cache)  role--权限拦截自行扩展
                Admin admin = authService.findByAdminId(userId);

                if (ObjectUtils.isEmpty(admin)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }

                return chain.filter(exchange.mutate().request(
                                handleRequest(request, new AppUser(userId, tenantId, admin.getUsername(), "", TokenLabelEnum.ADMIN.toString())))
                        .build());
            default:
                // 非管理类用户
                // 再次校验用户信息
                WechatUser user = authService.findByUserId(userId);

                if (ObjectUtils.isEmpty(user)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }

                return chain.filter(exchange.mutate().request(
                                handleRequest(request, new AppUser(userId, tenantId, user.getUsername(), user.getOpenid(), TokenLabelEnum.USER.toString())))
                        .build());
        }
    }

    private ServerHttpRequest handleRequest(ServerHttpRequest request, AppUser appUser) {
        // request parameters 添加用户信息 中文需要URIEncode
        URI uri = request.getURI();
        StringBuilder query = new StringBuilder();
        String originalQuery = uri.getRawQuery();

        if (StringUtils.hasText(originalQuery)) {
            query.append(originalQuery);
            if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                query.append('&');
            }
        }

        query.append("id=").append(appUser.getId())
                .append("&tenantId=").append(appUser.getTenantId())
                .append("&username=").append(URLEncoder.encode(appUser.getUsername(), StandardCharsets.UTF_8))
                .append("&openid=").append(appUser.getOpenid())
                .append("&label=").append(appUser.getLabel())
        ;

        return request.mutate()
                .uri(UriComponentsBuilder.fromUri(uri).replaceQuery(query.toString()).build(true).toUri())
                .build();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    // 不同平台用户  限制接口请求
    private void handleRequestPathValid(String path, TokenLabelEnum label) {

        boolean flag = true;

        switch (label) {
            case USER:
                if (PATH_MATCHER.match("/admin/**", path)) {
                    flag = false;
                }
        }

        if (!flag) {
            log.error("用户请求接口: {}, 账号类型: {}, 无权访问...", path, label);
            throw new AppException(RCode.ACCESS_DENY);
        }
    }
}
