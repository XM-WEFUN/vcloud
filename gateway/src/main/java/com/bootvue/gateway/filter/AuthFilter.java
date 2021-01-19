package com.bootvue.gateway.filter;

import cn.hutool.core.net.URLEncoder;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.config.app.Keys;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.User;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.UserMapperService;
import com.bootvue.core.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AppConfig appConfig;
    private final UserMapperService userMapperService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (CollectionUtils.isEmpty(appConfig.getSkipUrls())) {
            return chain.filter(exchange);
        }

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

        // access_token校验

        ServerHttpResponse resp = exchange.getResponse();
        String token = exchange.getRequest().getHeaders().getFirst("token");
        Claims claims = null;

        if (StringUtils.isEmpty(token) || !JwtUtil.isVerify(token) || ObjectUtils.isEmpty(claims = JwtUtil.decode(token))
                || !org.apache.commons.lang3.StringUtils.equalsIgnoreCase(AppConst.ACCESS_TOKEN, claims.get("type", String.class))) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        // 数据库再次校验用户信息 (cache)
        User user = userMapperService.findById(claims.get("user_id", Long.class));

        if (ObjectUtils.isEmpty(user)) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        // request header添加用户信息 & Xss 过滤
        String method = request.getMethodValue();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());

        // request header添加上用户信息  中文需要URIEncode
        headers.add("user_id", String.valueOf(user.getId()));
        headers.add("username", user.getUsername());
        headers.add("nickname", URLEncoder.createDefault().encode(user.getNickname(), StandardCharsets.UTF_8));
        headers.add("openid", user.getOpenid());
        headers.add("roles", user.getRoles());
        headers.add("phone", user.getPhone());
        headers.add("avatar", user.getAvatar());
        headers.add("tenant_code", user.getTenantCode());

        ServerHttpRequest newRequest = request.mutate().build();
        newRequest = new ServerHttpRequestDecorator(newRequest) {
            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }
        };
        return chain.filter(exchange.mutate().
                request(newRequest).
                build());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
