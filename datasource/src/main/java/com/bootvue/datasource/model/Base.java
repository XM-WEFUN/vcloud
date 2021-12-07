package com.bootvue.datasource.model;

import com.bootvue.common.serializer.LocalDateTimeSerializer;
import com.bootvue.common.serializer.LongToStringSerializer;
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
public class Base {
    @JsonSerialize(using = LongToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime deleteTime;
}
