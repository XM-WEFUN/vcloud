package com.bootvue.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bootvue.datasource.type.PlatformEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * oauth2客户端
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Oauth2Client implements Serializable {

    private static final long serialVersionUID = 637946546033655114L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * client id
     */
    private String clientId;

    /**
     * secret
     */
    private String secret;

    /**
     * code,password,refresh_token
     */
    private String grantType;

    /**
     * all || basic_info
     */
    private String scope;

    /**
     * 平台类型 0 WEB 1 APP 2 小程序
     */
    private PlatformEnum platform;

    /**
     * access token 有效时长 s
     */
    private Long accessTokenExpire;

    /**
     * refresh token 有效时长 s
     */
    private Long refreshTokenExpire;

    /**
     * 重定向url
     */
    private String redirectUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}