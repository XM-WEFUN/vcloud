package com.bootvue.admin.dto;

import com.bootvue.core.serializer.LocalDateTimeSerializer;
import com.bootvue.core.serializer.StatusSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryOut {
    private Long id;
    private String username;
    private String roleName;

    @JsonSerialize(using = StatusSerializer.class, nullsUsing = StatusSerializer.class)
    private Boolean status; // 状态  1 正常  0禁用

    @JsonSerialize(using = LocalDateTimeSerializer.class, nullsUsing = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
}
