package com.bootvue.gateway.filter;

import com.bootvue.common.config.AppConfig;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.R;
import com.bootvue.common.result.RCode;
import com.bootvue.common.util.JwtUtil;
import com.bootvue.gateway.util.XssStringJsonSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.jsonwebtoken.Claims;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.registerModule(new SimpleModule("XssStringJsonSerializer").addSerializer(new XssStringJsonSerializer()));
    }

    private final AppConfig appConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (CollectionUtils.isEmpty(appConfig.getSkipUrls())) {
            return chain.filter(exchange);
        }

        for (String skipUrl : appConfig.getSkipUrls()) {
            if (PATH_MATCHER.match(skipUrl, path)) {
                return chain.filter(exchange);
            }
        }

        ServerHttpResponse resp = exchange.getResponse();
        String token = exchange.getRequest().getHeaders().getFirst("token");
        Claims claims = null;

        if (StringUtils.isEmpty(token) || !JwtUtil.isVerify(token) || ObjectUtils.isEmpty(claims = JwtUtil.decode(token))) {
            return unAuth(resp, R.error(new AppException(RCode.UNAUTHORIZED_ERROR)));
        }

        // todo uri 权限控制

        // request header添加用户信息 & Xss 过滤
        String method = request.getMethodValue();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());

        // todo request header添加上用户信息
        headers.add("xxxx", "李四");

        // Xss过滤只处理 post put请求 & 不处理文件上传类型的
        if ((HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method)) &&
                !MediaType.MULTIPART_FORM_DATA.isCompatibleWith(request.getHeaders().getContentType())) {
            return DataBufferUtils.join(request.getBody()).flatMap(d -> Mono.just(Optional.of(d))).defaultIfEmpty(Optional.empty())
                    .flatMap(optional -> {
                        // 取出body中的参数
                        String bodyString = "";
                        if (optional.isPresent()) {
                            byte[] oldBytes = new byte[optional.get().readableByteCount()];
                            optional.get().read(oldBytes);
                            bodyString = new String(oldBytes, StandardCharsets.UTF_8);
                        }
                        try {
                            bodyString = StringUtils.isEmpty(bodyString) ? "" : objectMapper.writeValueAsString(objectMapper.readValue(bodyString, Object.class));
                        } catch (JsonProcessingException e) {
                            log.error("xss过滤异常", e);
                        }

                        ServerHttpRequest newRequest = request.mutate().uri(request.getURI()).build();

                        // 重新构造body
                        byte[] newBytes = bodyString.getBytes(StandardCharsets.UTF_8);
                        DataBuffer bodyDataBuffer = toDataBuffer(newBytes);
                        Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);

                        // 由于修改了传递参数，需要重新设置CONTENT_LENGTH，长度是字节长度，不是字符串长度
                        int length = newBytes.length;
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.remove(HttpHeaders.CONTENT_LENGTH);
                        headers.setContentLength(length);
                        // 重写ServerHttpRequestDecorator，修改了body和header，重写getBody和getHeaders方法
                        newRequest = new ServerHttpRequestDecorator(newRequest) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return bodyFlux;
                            }

                            @Override
                            public HttpHeaders getHeaders() {
                                return headers;
                            }
                        };

                        return chain.filter(exchange.mutate().request(newRequest).build());
                    });
        } else {
            // 只修改header
            ServerHttpRequest newRequest = request.mutate().build();
            newRequest = new ServerHttpRequestDecorator(newRequest) {
                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }
            };
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
    }


    private Mono<Void> unAuth(ServerHttpResponse resp, R r) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String result = "";
        try {
            result = objectMapper.writeValueAsString(r);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer buffer = resp.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }

    private DataBuffer toDataBuffer(byte[] bytes) {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
