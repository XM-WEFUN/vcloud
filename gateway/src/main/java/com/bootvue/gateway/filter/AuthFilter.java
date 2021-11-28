package com.bootvue.gateway.filter;

import com.bootvue.common.config.app.AppConfig;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import com.bootvue.common.util.JwtUtil;
import com.bootvue.datasource.entity.Oauth2Client;
import com.bootvue.datasource.entity.User;
import com.bootvue.gateway.service.Oauth2ClientMapperService;
import com.bootvue.gateway.service.UserMapperService;
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

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    private final AppConfig appConfig;
    private final UserMapperService userMapperService;
    private final Oauth2ClientMapperService oauth2ClientMapperService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // *************1************** uri校验
        if (!CollectionUtils.isEmpty(appConfig.getSkipUrls())) {
            for (String skipUrl : appConfig.getSkipUrls()) {
                if (PATH_MATCHER.match(skipUrl, path)) {
                    return chain.filter(exchange);
                }
            }
        }

        // *************2************ 校验token
        String token = "";
        try {
            token = request.getHeaders().getFirst(AppConst.REQUEST_HEADER_TOKEN).substring("Bearer ".length());
        } catch (Exception e) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        if (!StringUtils.hasText(token) || !JwtUtil.isVerify(token)) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        Claims claims = JwtUtil.decode(token);

        // ***********3********** 用户与oauth2信息校验
        Long id = claims.get("id", Long.class);
        User user = userMapperService.findById(id);
        if (ObjectUtils.isEmpty(user) || !user.getStatus() || !ObjectUtils.isEmpty(user.getDeleteTime())) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        Long oauth2Id = claims.get("oauth2_id", Long.class);
        Oauth2Client client = oauth2ClientMapperService.findById(oauth2Id);
        if (ObjectUtils.isEmpty(client) || !ObjectUtils.isEmpty(client.getDeleteTime()) || !user.getTenantId().equals(client.getTenantId())) {
            throw new AppException(RCode.UNAUTHORIZED_ERROR);
        }

        // *********4*********** request parameters 添加用户信息 中文需要URIEncode
        URI uri = request.getURI();
        StringBuilder query = new StringBuilder();
        String originalQuery = uri.getRawQuery();

        if (StringUtils.hasText(originalQuery)) {
            query.append(originalQuery);
            if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                query.append('&');
            }
        }

        query.append("id=").append(user.getId())
                .append("&tenantId=").append(user.getTenantId())
                .append("&account=").append(user.getAccount())
                .append("&type=").append(user.getType().getValue())
        ;

        return chain.filter(exchange.mutate().request(request.mutate()
                .uri(UriComponentsBuilder.fromUri(uri).replaceQuery(query.toString())
                        .build(true).toUri())
                .build()).build());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
