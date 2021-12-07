package com.bootvue.admin.dto;

import com.bootvue.common.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    @JsonSerialize(using = LongToStringSerializer.class)
    private Long id;

    private String name;
}
