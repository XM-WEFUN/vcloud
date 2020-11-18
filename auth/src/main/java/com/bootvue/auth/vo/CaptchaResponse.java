package com.bootvue.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class CaptchaResponse implements Serializable {
    private static final long serialVersionUID = -8913185071017742461L;
    private String key;
    private String image;
}
