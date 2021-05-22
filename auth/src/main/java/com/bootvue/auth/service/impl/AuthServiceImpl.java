package com.bootvue.auth.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.bootvue.auth.dto.*;
import com.bootvue.auth.service.AuthService;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.config.app.Keys;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.ddo.menu.MenuDo;
import com.bootvue.core.entity.Action;
import com.bootvue.core.entity.Tenant;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import com.bootvue.core.module.Token;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.ActionMapperService;
import com.bootvue.core.service.MenuMapperService;
import com.bootvue.core.service.TenantMapperService;
import com.bootvue.core.service.UserMapperService;
import com.bootvue.core.util.JwtUtil;
import com.bootvue.core.util.RsaUtil;
import com.bootvue.core.wechat.WechatApi;
import com.bootvue.core.wechat.WechatUtil;
import com.bootvue.core.wechat.vo.WechatSession;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {
    private static final LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

    private final RedissonClient redissonClient;
    private final AppConfig appConfig;
    private final UserMapperService userMapperService;
    private final TenantMapperService tenantMapperService;
    private final UserMapper userMapper;
    private final MenuMapperService menuMapperService;
    private final ActionMapperService actionMapperService;

    @Override
    public AuthResponse authentication(Credentials credentials) {

        switch (credentials.getType()) {
            case USERNAME_PASSWORD_LOGIN:
                return handleCommonLogin(credentials);
            case SMS_LOGIN:
                return handleSmsLogin(credentials);
            case REFRESH_TOKEN:
                return handleRefreshToken(credentials);
            case WECHAT:
                return handleWechatLogin(credentials);
            default:
                throw new AppException(RCode.PARAM_ERROR.getCode(), "认证方式错误");
        }
    }

    /**
     * 微信小程序认证
     *
     * @param credentials 小程序相关参数
     * @return AuthResponse
     */
    private AuthResponse handleWechatLogin(Credentials credentials) {
        try {
            // 1. 获取openid与session_key
            Keys keys = AppConfig.getKeys(appConfig, credentials.getPlatform());
            WechatParams wechat = credentials.getWechat();

            WechatSession wechatSession = WechatApi.code2Session(wechat.getCode(), keys.getWechatAppid(), keys.getWechatSecret());
            if (ObjectUtils.isEmpty(wechatSession) || !StringUtils.hasText(wechatSession.getOpenid()) || !StringUtils.hasText(wechatSession.getSessionKey())) {
                throw new AppException(RCode.PARAM_ERROR);
            }

            // 2. 校验数据签名
            if (!DigestUtils.sha1Hex(wechat.getRawData() + wechatSession.getSessionKey()).equalsIgnoreCase(wechat.getSignature())) {
                log.error("微信小程序加密参数校验失败");
                throw new AppException(RCode.PARAM_ERROR);
            }

            // 3. 敏感数据暂不处理
            log.info("加密数据: {}", WechatUtil.decrypt(wechatSession.getSessionKey(), wechat.getEncryptedData(), wechat.getIv()));

            // 4. 用户是否存在  新增/更新用户
            User user = userMapperService.findByOpenid(wechatSession.getOpenid(), credentials.getTenantCode());

            if (ObjectUtils.isEmpty(user)) {
                Tenant tenant = tenantMapperService.findByTenantCode(credentials.getTenantCode());
                user = new User(null, "wx_" + RandomUtil.randomString(5) + wechat.getNickName().substring(0, 2).trim(),
                        RandomUtil.randomString(32), tenant.getId(), -1L, "", wechatSession.getOpenid(), wechat.getNickName(),
                        wechat.getAvatarUrl(), true, LocalDateTime.now(), null, null);
                userMapper.insert(user);
            } else {
                user.setAvatar(wechat.getAvatarUrl());
                user.setNickname(wechat.getNickName());
                user.setUpdateTime(LocalDateTime.now());
                userMapper.updateById(user);
            }

            return getAuthResponse(user);

        } catch (Exception e) {
            log.error("微信小程序用户认证失败: 参数: {}", credentials);
            throw new AppException(RCode.PARAM_ERROR);
        }

    }

    @Override
    public void handleSmsCode(PhoneParams phoneParams) {
        // 校验手机号是否存在
        User user = userMapperService.findByPhone(phoneParams.getPhone(), phoneParams.getTenantCode());
        if (ObjectUtils.isEmpty(user)) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        String code = RandomUtil.randomNumbers(6);
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, phoneParams.getPhone()));
        bucket.set(code, 15L, TimeUnit.MINUTES);
        log.info("短信验证码 : {}", code);
    }

    @Override
    public CaptchaResponse getCaptcha() {
        lineCaptcha.createCode();
        String code = lineCaptcha.getCode();
        String key = RandomStringUtils.randomAlphanumeric(12);
        String image = "data:image/png;base64," + lineCaptcha.getImageBase64();
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.CAPTCHA_KEY, key));
        bucket.set(code, 10, TimeUnit.MINUTES);

        return new CaptchaResponse(key, image);
    }

    /**
     * 换取新的access_token
     * refresh_token也一并更新
     *
     * @param credentials refresh_token等
     * @return AuthResponse
     */
    private AuthResponse handleRefreshToken(Credentials credentials) {
        if (!JwtUtil.isVerify(credentials.getRefreshToken())) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "refresh_token无效");
        }

        Claims claims = JwtUtil.decode(credentials.getRefreshToken());
        String type = claims.get("type", String.class);
        if (!StringUtils.hasText(type) || !AppConst.REFRESH_TOKEN.equalsIgnoreCase(type)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "refresh_token无效");
        }

        // 用户信息
        Long userId = claims.get("user_id", Long.class);
        User user = userMapperService.findById(userId);
        if (ObjectUtils.isEmpty(user)) {
            user = userMapper.selectById(userId);
        }
        return getAuthResponse(user);
    }

    /**
     * 短信验证码登录
     *
     * @param credentials 短信验证码
     * @return AuthResponse
     */
    private AuthResponse handleSmsLogin(Credentials credentials) {
        // 验证手机验证码 与 手机号
        if (!StringUtils.hasText(credentials.getCode()) || !StringUtils.hasText(credentials.getTenantCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, credentials.getPhone()));
        String code = bucket.get();
        if (!StringUtils.hasText(code) || !credentials.getCode().equals(code)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "验证码错误");
        }
        // 验证通过删除code
        bucket.delete();

        return getAuthResponse(userMapperService.findByPhone(credentials.getPhone(), credentials.getTenantCode()));
    }

    /**
     * 用户名  密码  图形验证码登录
     *
     * @param credentials 用户信息
     * @return AuthResponse
     */
    private AuthResponse handleCommonLogin(Credentials credentials) {

        if (!StringUtils.hasText(credentials.getKey()) || !StringUtils.hasText(credentials.getUsername()) ||
                !StringUtils.hasText(credentials.getPassword()) || !StringUtils.hasText(credentials.getCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        // 校验验证码
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.CAPTCHA_KEY, credentials.getKey()));
        String storedCode = bucket.getAndDelete();
        if (!StringUtils.hasText(storedCode) || !credentials.getCode().equalsIgnoreCase(storedCode)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "验证码无效");
        }

        String password = RsaUtil.getPassword(appConfig, credentials.getPlatform(), credentials.getPassword());
        // 验证 用户名 密码
        User user = userMapperService.findByUsernameAndPassword(credentials.getUsername(),
                DigestUtils.md5Hex(password),
                credentials.getTenantCode());

        return getAuthResponse(user);
    }

    /**
     * 用户信息&token
     *
     * @param user user对象
     * @return AuthResponse
     */
    private AuthResponse getAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
        if (ObjectUtils.isEmpty(user) || user.getRoleId().equals(0L)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "用户凭证错误");
        }

        // 响应token信息
        Token accessToken = new Token();
        Token refreshToken = new Token();
        BeanUtils.copyProperties(user, accessToken);
        BeanUtils.copyProperties(user, refreshToken);
        accessToken.setUserId(user.getId());
        accessToken.setType(AppConst.ACCESS_TOKEN);
        refreshToken.setUserId(user.getId());
        refreshToken.setType(AppConst.REFRESH_TOKEN);

        BeanUtils.copyProperties(user, response);

        //  access_token 7200s
        LocalDateTime accessTokenExpire = LocalDateTime.now().plusSeconds(7200L);
        // refresh_token 20d
        LocalDateTime refreshTokenExpire = LocalDateTime.now().plusDays(20L);

        String accessTokenStr = JwtUtil.encode(accessTokenExpire, BeanUtil.beanToMap(accessToken, true, true));
        String refreshTokenStr = JwtUtil.encode(refreshTokenExpire, BeanUtil.beanToMap(refreshToken, true, true));

        response.setAccessToken(accessTokenStr);
        response.setRefreshToken(refreshTokenStr);

        // 菜单权限信息
        Stopwatch stopwatch = Stopwatch.createStarted();
        response.setMenus(getMenus(user.getId(), user.getRoleId()));
        log.info("菜单权限处理耗时: {} s", stopwatch.stop().elapsed(TimeUnit.SECONDS));

        return response;
    }


    /**
     * 获取用户菜单 权限
     *
     * @param userId
     * @param roleId
     * @return
     */
    private List<MenuOut> getMenus(Long userId, Long roleId) {
        List<MenuOut> outs = new ArrayList<>();

        // 父一级菜单
        List<MenuDo> menuDos = menuMapperService.getMenuList(userId, roleId, 0L);

        menuDos.stream().forEach(e -> {  // 父级
            MenuOut menuOut = new MenuOut();
            BeanUtils.copyProperties(e, menuOut);

            // 父级权限
            Set<String> ps = getPermissions(e.getActionIds());
            menuOut.setPermissions(ps);
            if (!CollectionUtils.isEmpty(ps)) {
                // 子级菜单
                List<MenuDo> subMenuDos = menuMapperService.getMenuList(userId, roleId, e.getId());
                List<MenuOut> children = subMenuDos.stream().map(i -> { // 子级
                    MenuOut subMenuOut = new MenuOut();
                    BeanUtils.copyProperties(i, subMenuOut);
                    // 子级的权限
                    subMenuOut.setPermissions(getPermissions(i.getActionIds()));
                    return subMenuOut;
                }).collect(Collectors.toList());

                menuOut.setChildren(children);

                outs.add(menuOut);
            }
        });
        return outs;
    }

    /**
     * 获取权限表达式 见readme文档
     * ["index:add,delete,update,list"]
     *
     * @param actionIds
     * @return
     */
    private Set<String> getPermissions(String actionIds) {
        Set<String> permissions = new HashSet<>();
        if (("0").equals(actionIds)) {
            permissions.add("list");
        } else if (!("-1").equals(actionIds) && StringUtils.hasText(actionIds)) {
            // 拼装actions
            List<Action> actions = actionMapperService.getActions(Splitter.on(",").trimResults().omitEmptyStrings()
                    .splitToStream(actionIds).mapToLong(Long::parseLong).boxed().collect(Collectors.toSet()));

            Map<String, Set<String>> rs = new HashMap<>();

            actions.stream().forEach(e -> {
                List<String> strings = Splitter.on(":").trimResults().omitEmptyStrings().splitToList(e.getAction());
                if (CollectionUtils.isEmpty(rs.get(strings.get(0)))) {
                    Set<String> action = new HashSet<>();
                    action.add(strings.get(1));
                    rs.put(strings.get(0), action);
                } else {
                    rs.get(strings.get(0)).add(strings.get(1));
                }
            });

            // rs转字符串
            rs.entrySet().stream().forEach(e -> {
                permissions.add(e.getKey() + ":" + Joiner.on(",").skipNulls().join(e.getValue()));
            });
        }

        return permissions;
    }

}
