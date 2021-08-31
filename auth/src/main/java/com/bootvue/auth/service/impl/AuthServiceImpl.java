package com.bootvue.auth.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.bootvue.auth.UserInfo;
import com.bootvue.auth.dto.*;
import com.bootvue.auth.service.AuthService;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.GenderEnum;
import com.bootvue.core.constant.PlatformType;
import com.bootvue.core.dto.RoleMenuDo;
import com.bootvue.core.entity.Admin;
import com.bootvue.core.entity.Menu;
import com.bootvue.core.entity.Tenant;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import com.bootvue.core.model.Token;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.AdminMapperService;
import com.bootvue.core.service.MenuMapperService;
import com.bootvue.core.service.TenantMapperService;
import com.bootvue.core.service.UserMapperService;
import com.bootvue.core.util.JwtUtil;
import com.bootvue.core.wechat.WechatApi;
import com.bootvue.core.wechat.WechatUtil;
import com.bootvue.core.wechat.vo.WechatSession;
import com.google.common.base.Splitter;
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
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {
    private static final LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

    private final RedissonClient redissonClient;
    private final AppConfig appConfig;
    private final AdminMapperService adminMapperService;
    private final TenantMapperService tenantMapperService;
    private final UserMapper userMapper;
    private final UserMapperService userMapperService;
    private final MenuMapperService menuMapperService;

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
            WechatParams wechat = credentials.getWechat();
            log.info("微信用户风险等级: {}", wechat.getRiskRank());
            WechatSession wechatSession = WechatApi.code2Session(wechat.getCode(), appConfig.getWechatAppid(), appConfig.getWechatSecret());
            // session_key暂时无用
            if (ObjectUtils.isEmpty(wechatSession) || !StringUtils.hasText(wechatSession.getOpenid()) || !StringUtils.hasText(wechatSession.getSessionKey())) {
                throw new AppException(RCode.PARAM_ERROR);
            }
            //  校验数据签名
            if (!WechatUtil.getSignature(wechatSession.getSessionKey(), wechat.getRawData()).equalsIgnoreCase(wechat.getSignature())) {
                log.error("微信小程序加密参数校验失败, 计算签名: {}, 用户参数签名: {}", WechatUtil.getSignature(wechatSession.getSessionKey(), wechat.getRawData()), wechat.getSignature());
                throw new AppException(RCode.PARAM_ERROR);
            }

            //  加密数据
            JSONObject encryptData = WechatUtil.decrypt(wechatSession.getSessionKey(), wechat.getEncryptedData(), wechat.getIv());
            log.info("加密数据: {}", encryptData);
            // 敏感数据有效性校验
            JSONObject watermark = encryptData.getJSONObject("watermark");
            if (!appConfig.getWechatAppid().equalsIgnoreCase(watermark.getString("appid")) ||
                    Math.abs(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).getEpochSecond() - watermark.getLong("timestamp")) > 2 * 60) {
                log.error("加密数据appid不符或水印时间戳时间不符, {}", encryptData);
                throw new AppException(RCode.PARAM_ERROR);
            }

            // 用户是否存在  不存在新增用户
            Tenant tenant = tenantMapperService.findByTenantCode(credentials.getTenantCode());
            User user = userMapperService.findByOpenidAndTenantId(wechatSession.getOpenid(), tenant.getId());

            String country = StringUtils.hasText(encryptData.getString("country")) ? encryptData.getString("country") : "";
            String avatar = StringUtils.hasText(encryptData.getString("avatarUrl")) ? encryptData.getString("avatarUrl") : "";
            String nickname = StringUtils.hasText(encryptData.getString("nickName")) ? encryptData.getString("nickName") : "wx_" + RandomStringUtils.randomAlphabetic(8);
            String province = StringUtils.hasText(encryptData.getString("province")) ? encryptData.getString("province") : "";
            String city = StringUtils.hasText(encryptData.getString("city")) ? encryptData.getString("city") : "";
            GenderEnum gender = GenderEnum.find(encryptData.getIntValue("gender"));

            if (ObjectUtils.isEmpty(user)) {
                // 新增小程序用户
                user = new User(null, tenant.getId(), nickname, wechatSession.getOpenid(), "", avatar, gender,
                        country, province, city, true, "", LocalDateTime.now(), null);
                userMapper.insert(user);
            } else {
                // 更新用户信息
                user.setUsername(nickname);
                user.setAvatar(avatar);
                user.setGender(gender);
                user.setCountry(country);
                user.setProvince(province);
                user.setCity(city);
                user.setUpdateTime(LocalDateTime.now());
                userMapperService.updateUser(user);
            }

            return getAuthResponse(new UserInfo(user.getId(), user.getUsername(), user.getPhone(), user.getAvatar(), user.getGender(), user.getTenantId(), PlatformType.CUSTOMER, ""));

        } catch (Exception e) {
            log.error("微信小程序用户认证失败: 参数: {}", credentials);
            throw new AppException(RCode.PARAM_ERROR);
        }

    }

    @Override
    public void handleSmsCode(PhoneParams phoneParams) {
        // 校验手机号是否存在
        Admin admin = adminMapperService.findByPhoneAndTenantCode(phoneParams.getPhone(), phoneParams.getTenantCode());
        if (ObjectUtils.isEmpty(admin)) {
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
        Long roleId = claims.get(AppConst.HEADER_ROLEID, Long.class);
        Long id = claims.get(AppConst.HEADER_USER_ID, Long.class);
        PlatformType platform = PlatformType.getPlatform(claims.get(AppConst.HEADER_PLATFORM, Integer.class));

        switch (platform) {
            case AGENT: // 运营平台||代理平台
            case ADMIN:
                Admin admin = adminMapperService.findById(id);
                if (ObjectUtils.isEmpty(admin)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }
                return getAuthResponse(new UserInfo(id, admin.getUsername(), admin.getPhone(), admin.getAvatar(), GenderEnum.UNKNOWN, admin.getTenantId(), platform, admin.getRoleIds()));
            default: // 其它平台
                User user = userMapperService.findById(id);
                if (ObjectUtils.isEmpty(user)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }
                return getAuthResponse(new UserInfo(id, user.getUsername(), user.getPhone(), user.getAvatar(), user.getGender(), user.getTenantId(), platform, ""));
        }
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

        // platform类型
        switch (credentials.getPlatform()) {
            case ADMIN: // 运营平台
            case AGENT: // 代理平台
                Admin admin = adminMapperService.findByPhoneAndTenantCode(credentials.getPhone(), credentials.getTenantCode());
                Assert.notNull(admin, RCode.PARAM_ERROR.getMsg());
                return getAuthResponse(new UserInfo(admin.getId(), admin.getUsername(), admin.getPhone(), admin.getAvatar(), GenderEnum.UNKNOWN, admin.getTenantId(), credentials.getPlatform(), admin.getRoleIds()));
            case CUSTOMER:
                log.info("handle customer login....");
                return null;
            default:
                throw new AppException(RCode.PARAM_ERROR);
        }
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

        //String password = RsaUtil.getPassword(appConfig, credentials.getPlatform(), credentials.getPassword());
        String password = "123456";

        // platform类型
        switch (credentials.getPlatform()) {
            case ADMIN: // 运营平台
            case AGENT:
                // 验证 用户名 密码
                Admin admin = adminMapperService.findByUsernameAndPassword(credentials.getUsername(),
                        DigestUtils.md5Hex(password),
                        credentials.getTenantCode());
                Assert.notNull(admin, RCode.PARAM_ERROR.getMsg());
                return getAuthResponse(new UserInfo(admin.getId(), admin.getUsername(), admin.getPhone(), admin.getAvatar(), GenderEnum.UNKNOWN, admin.getTenantId(), credentials.getPlatform(), admin.getRoleIds()));
            case CUSTOMER:
                log.info("handle customer login....");
                return null;
            default:
                throw new AppException(RCode.PARAM_ERROR);
        }
    }

    /**
     * 用户信息&token
     *
     * @param info userinfo对象
     * @return AuthResponse
     */
    private AuthResponse getAuthResponse(UserInfo info) {
        // 响应token信息
        Token accessToken = new Token();
        Token refreshToken = new Token();

        BeanUtils.copyProperties(info, accessToken);
        BeanUtils.copyProperties(info, refreshToken);
        accessToken.setType(AppConst.ACCESS_TOKEN);
        refreshToken.setType(AppConst.REFRESH_TOKEN);
        accessToken.setPlatform(info.getPlatform().getValue());
        refreshToken.setPlatform(info.getPlatform().getValue());

        //  access_token 7200s
        LocalDateTime accessTokenExpire = LocalDateTime.now().plusSeconds(7200L).plusMinutes(5L);
        // refresh_token 180d
        LocalDateTime refreshTokenExpire = LocalDateTime.now().plusDays(180L).plusMinutes(5L);

        String accessTokenStr = JwtUtil.encode(accessTokenExpire, BeanUtil.beanToMap(accessToken, true, true));
        String refreshTokenStr = JwtUtil.encode(refreshTokenExpire, BeanUtil.beanToMap(refreshToken, true, true));

        // response对象
        AuthResponse response = new AuthResponse();

        response.setUsername(info.getUsername());
        response.setPhone(info.getPhone());
        response.setAvatar(info.getAvatar());
        response.setGender(info.getGender().getValue());
        response.setAccessToken(accessTokenStr);
        response.setRefreshToken(refreshTokenStr);

        // 菜单
        if (!info.getPlatform().equals(PlatformType.CUSTOMER) && StringUtils.hasText(info.getRoleIds())) {
            response.setMenus(getMenu(info.getRoleIds()));
        }
        return response;
    }

    // 获取角色菜单信息
    private List<MenuOut> getMenu(String roleIds) {
        List<String> ids = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(roleIds);
        List<RoleMenuDo> roleMenuDos = menuMapperService.findMenuIdByRoleId(ids);

        if (CollectionUtils.isEmpty(roleMenuDos)) {
            return null;
        }

        // 角色菜单并集
        Set<Long> menus = new HashSet<>();

        roleMenuDos.stream().forEach(roleMenuDo -> {
            Set<Long> menuIds = Splitter.on(",").trimResults().omitEmptyStrings().splitToStream(roleMenuDo.getMenuIds()).mapToLong(Long::parseLong).boxed().collect(Collectors.toSet());
            menus.addAll(menuIds);
        });

        if (CollectionUtils.isEmpty(menus)) {
            return null;
        }

        List<MenuOut> out = new ArrayList<>();

        List<Menu> ms = menuMapperService.findMenuByMenuId(menus);
        for (Menu m : ms) {
            if (!m.getPId().equals(0L)) {
                continue;
            }
            // 子菜单
            List<MenuOut> child = ms.stream()
                    .filter(i -> i.getPId().equals(m.getId()))
                    .map(x -> new MenuOut(x.getKey(), x.getIcon(), x.getTitle(), x.getPath(), x.getShow(), x.getDefaultSelect(), x.getDefaultOpen(), null))
                    .collect(Collectors.toList());

            out.add(new MenuOut(m.getKey(), m.getIcon(), m.getTitle(), m.getPath(), m.getShow(), m.getDefaultSelect(), m.getDefaultOpen(), child));
        }
        return out;
    }

}
