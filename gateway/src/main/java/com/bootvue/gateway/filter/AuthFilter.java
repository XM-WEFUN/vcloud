package com.bootvue.gateway.filter;

import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.config.app.Key;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.PlatformType;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.entity.User;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.AdminMapperService;
import com.bootvue.core.service.UserMapperService;
import com.bootvue.core.util.JwtUtil;
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

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AppConfig appConfig;
    private final AdminMapperService adminMapperService;
    private final UserMapperService userMapperService;

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
                || !AppConst.ACCESS_TOKEN.equalsIgnoreCase(claims.get("type", String.class))) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        PlatformType platform = PlatformType.getPlatform(claims.get(AppConst.HEADER_PLATFORM, Integer.class)); // 账号所属平台
        Long userId = claims.get(AppConst.HEADER_USER_ID, Long.class);

        handleRequestPathValid(path, platform);  // 接口验证

        switch (platform) {
            case ADMIN:
                // 再次校验用户信息 (cache)  role--权限拦截自行扩展
                Admin admin = adminMapperService.findById(userId);

                if (ObjectUtils.isEmpty(admin)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }

                return chain.filter(exchange.mutate().request(
                                handleRequest(request, platform, admin.getId(), "", admin.getUsername()))
                        .build());
            default:
                // 用户平台
                // 再次校验用户信息
                User user = userMapperService.findById(userId);

                if (ObjectUtils.isEmpty(user)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }

                return chain.filter(exchange.mutate().request(
                                handleRequest(request, platform, user.getId(), user.getOpenid(), user.getUsername()))
                        .build());
        }
    }

    private ServerHttpRequest handleRequest(ServerHttpRequest request, PlatformType platform, Long id, String openid, String username) {
        // request header添加用户信息
        // request header添加上用户信息  中文需要URIEncode

        return request.mutate().headers((headers) -> {
            headers.add(AppConst.HEADER_USER_ID, String.valueOf(id));
            headers.add(AppConst.HEADER_OPENID, openid);
            headers.add(AppConst.HEADER_USERNAME, String.valueOf(URLEncoder.encode(username, StandardCharsets.UTF_8)));
            headers.add(AppConst.HEADER_PLATFORM, String.valueOf(platform.getValue()));
        }).build();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    // 不同平台用户  限制接口请求
    private void handleRequestPathValid(String path, PlatformType platform) {

        boolean flag = true;

        switch (platform) {
            case ADMIN:
                if (!PATH_MATCHER.match("/auth/**", path) &&
                        !PATH_MATCHER.match("/admin/**", path)) {
                    flag = false;
                }
                break;
            default:
                if (!PATH_MATCHER.match("/auth/**", path)) {
                    flag = false;
                }
        }

        if (!flag) {
            log.error("用户请求接口: {}, 所属平台: {}, 无权访问...", path, platform);
            throw new AppException(RCode.ACCESS_DENY);
        }
    }
}
