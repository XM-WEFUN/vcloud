package com.bootvue.common.config.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cache {
    private String cacheName;  // redis cache 名
    private Long ttl;           // 最大生存时间
    private Long maxIdleTime;   // 最大空闲时间
}
