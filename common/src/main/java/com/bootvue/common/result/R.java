package com.bootvue.common.result;

import lombok.*;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> R<T> success(T data) {
        return new R<>(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg(), data);
    }

    public static <T> R<T> success() {
        return success(null);
    }

    public static <T> R<T> error(AppException e) {
        return new R<>(e.getCode(), e.getMsg(), null);
    }

    public static void handleErr(BindingResult result) {
        if (result.hasErrors()) {
            String msg = Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
            throw new AppException(RCode.PARAM_ERROR.getCode(), msg);
        }
    }
}
