package com.bootvue.web.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义 sentinel针对来源控制
 * 开启需要注入到IOC容器中
 */
@Slf4j
public class AppRequestOrignParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        return null;
    }
}
