package com.bootvue.core.config.app;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Caches {
    private String cacheName;
    private Long ttl;
    private Long maxIdleTime;
}
