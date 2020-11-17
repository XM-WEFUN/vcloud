package com.bootvue.gateway.filter;

import com.bootvue.common.config.AppConfig;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.R;
import com.bootvue.common.result.RCode;
import com.bootvue.common.util.JwtUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
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

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthFilter implements GlobalFilter, Ordered {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
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

        if (StringUtils.isEmpty(token) || !JwtUtil.isVerify(token) || ObjectUtils.isEmpty(JwtUtil.decode(token))) {
            return unAuth(resp, R.error(new AppException(RCode.UNAUTHORIZED_ERROR)));
        }

        // request header添加用户信息 & Xss 过滤
        String method = request.getMethodValue();
        HttpHeaders headers = new HttpHeaders();
        // todo request header添加上用户信息
        headers.putAll(request.getHeaders());

        headers.add("xxxx", "李四");

        // Xss过滤只处理 post put请求 & 不处理文件上传类型的
        if ((HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method)) &&
                request.getHeaders().getContentType().equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            return DataBufferUtils.join(request.getBody())
                    .flatMap(dataBuffer -> {
                        byte[] oldBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(oldBytes);
                        String bodyString = new String(oldBytes, StandardCharsets.UTF_8);
                        log.info("原请求参数为：{}", bodyString);
                        bodyString = StringEscapeUtils.escapeHtml4(bodyString);
                        log.info("修改后参数为：{}", bodyString);

                        byte[] newBytes = bodyString.getBytes(StandardCharsets.UTF_8);
                        DataBuffer bodyDataBuffer = toDataBuffer(newBytes);
                        Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);


                        int length = newBytes.length;
                        headers.remove(HttpHeaders.CONTENT_LENGTH);
                        headers.setContentLength(length);
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        ServerHttpRequest newRequest = request.mutate().build();

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
