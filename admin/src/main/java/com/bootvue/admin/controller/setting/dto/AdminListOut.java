package com.bootvue.admin.controller.setting.dto;

import com.bootvue.core.serializer.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminListOut {
    private Long id;
    private String username;
    private String phone;
    private Boolean status;
    @JsonSerialize(using = LocalDateTimeSerializer.class, nullsUsing = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class, nullsUsing = LocalDateTimeSerializer.class)
    private LocalDateTime deleteTime;
}

