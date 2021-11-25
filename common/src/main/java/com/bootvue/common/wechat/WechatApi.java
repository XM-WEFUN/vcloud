package com.bootvue.common.wechat;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import com.bootvue.common.wechat.vo.WechatSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

// 微信相关接口
@Slf4j
public class WechatApi {
    /**
     * code-->openid session_key
     *
     * @param code   code
     * @param appid  小程序账号appid
     * @param secret secret
     * @return WechatSession
     */
    public static WechatSession code2Session(String code, String appid, String secret) {
        if (!StringUtils.hasText(code) || !StringUtils.hasText(appid) || !StringUtils.hasText(secret)) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        String res = HttpUtil.get(String.format("", appid, secret, code));
        log.info("微信小程序 code2Session接口 code: {} 响应: {}", code, res);
        return JSON.parseObject(res, WechatSession.class);
    }

}
