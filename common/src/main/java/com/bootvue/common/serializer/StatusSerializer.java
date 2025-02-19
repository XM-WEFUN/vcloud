package com.bootvue.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StatusSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value != null && value ? "正常" : "禁用");
    }

}
