package com.bootvue.core.config.app;

import lombok.Data;

@Data
public class Cache {
    private String cacheName;
    private Long ttl;
    private Long maxIdleTime;
}
