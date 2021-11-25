package com.bootvue.gateway.filter;

import com.bootvue.common.config.app.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        return chain.filter(exchange);

       /* return chain.filter(exchange.mutate().request(
                        handleRequest(request, null))
                .build());*/

    }

    private ServerHttpRequest handleRequest(ServerHttpRequest request, Object o) {
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

        /*query.append("id=").append(appUser.getId())
                .append("&tenantId=").append(appUser.getTenantId())
                .append("&username=").append(URLEncoder.encode(appUser.getUsername(), StandardCharsets.UTF_8))
                .append("&openid=").append(appUser.getOpenid())
                .append("&label=").append(appUser.getLabel())
        ;*/

        return request.mutate()
                .uri(UriComponentsBuilder.fromUri(uri).replaceQuery(query.toString()).build(true).toUri())
                .build();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
