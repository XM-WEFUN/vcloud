package com.bootvue.auth.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bootvue.auth.dto.*;
import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.service.mapper.*;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.constant.TokenLabelEnum;
import com.bootvue.core.model.Token;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.util.AppUtil;
import com.bootvue.core.util.JwtUtil;
import com.bootvue.core.util.RsaUtil;
import com.bootvue.core.wechat.WechatApi;
import com.bootvue.core.wechat.WechatUtil;
import com.bootvue.core.wechat.vo.WechatSession;
import com.bootvue.db.entity.Menu;
import com.bootvue.db.entity.*;
import com.bootvue.db.type.GenderEnum;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.annotations.Param;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
    private final WechatUserMapperService wechatUserMapperService;
    private final RoleMenuMapperService roleMenuMapperService;
    private final MenuMapperService menuMapperService;
    private final RoleAdminMapperService roleAdminMapperService;

    @Cacheable(cacheNames = AppConst.ADMIN_CACHE, key = "#id", unless = "#result==null")
    public Admin findByAdminId(Long id) {
        return adminMapperService.getOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getId, id)
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime));
    }

    @Cacheable(cacheNames = AppConst.WECHAT_USER_CACHE, key = "#id", unless = "#result==null")
    public WechatUser findByUserId(Long id) {
        return wechatUserMapperService.getOne(new QueryWrapper<WechatUser>()
                .lambda()
                .eq(WechatUser::getId, id)
                .eq(WechatUser::getStatus, true)
                .isNull(WechatUser::getDeleteTime));
    }

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
        if (!StringUtils.hasText(credentials.getTenantCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
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
            Tenant tenant = getTenant(credentials.getTenantCode());

            Assert.notNull(tenant, "租户不存在");
            WechatUser user = wechatUserMapperService.getOne(new QueryWrapper<WechatUser>()
                    .lambda()
                    .eq(WechatUser::getTenantId, tenant.getId())
                    .eq(WechatUser::getOpenid, wechatSession.getOpenid())
                    .eq(WechatUser::getStatus, true)
                    .isNull(WechatUser::getDeleteTime)
            );

            String country = StringUtils.hasText(encryptData.getString("country")) ? encryptData.getString("country") : "";
            String avatar = StringUtils.hasText(encryptData.getString("avatarUrl")) ? encryptData.getString("avatarUrl") : "";
            String nickname = StringUtils.hasText(encryptData.getString("nickName")) ? encryptData.getString("nickName") : "wx_" + RandomStringUtils.randomAlphabetic(8);
            String province = StringUtils.hasText(encryptData.getString("province")) ? encryptData.getString("province") : "";
            String city = StringUtils.hasText(encryptData.getString("city")) ? encryptData.getString("city") : "";
            GenderEnum gender = GenderEnum.find(encryptData.getIntValue("gender"));

            if (ObjectUtils.isEmpty(user)) {
                // 新增小程序用户
                user = new WechatUser(null, tenant.getId(), nickname, wechatSession.getOpenid(), "", avatar, gender, country, province,
                        city, true, "", LocalDateTime.now(), null, null);
                wechatUserMapperService.save(user);
            } else {
                // 更新用户信息
                user.setUsername(nickname);
                user.setAvatar(avatar);
                user.setGender(gender);
                user.setCountry(country);
                user.setProvince(province);
                user.setCity(city);
                user.setUpdateTime(LocalDateTime.now());
                wechatUserMapperService.updateById(user);
            }

            return getAuthResponse(new UserInfo(user.getId(), tenant.getId(), user.getUsername(), user.getPhone(),
                    user.getAvatar(), user.getGender(), TokenLabelEnum.USER, null));

        } catch (Exception e) {
            log.error("微信小程序用户认证失败: 参数: {}", credentials);
            throw new AppException(RCode.PARAM_ERROR);
        }

    }

    @Override
    public void handleSmsCode(PhoneParams phoneParams) {
        // 校验手机号是否存在
        Tenant tenant = getTenant(phoneParams.getTenantCode());

        Assert.notNull(tenant, "租户不存在");
        Admin admin = adminMapperService.getOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getTenantId, tenant.getId())
                .eq(Admin::getPhone, phoneParams.getPhone())
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime));
        if (ObjectUtils.isEmpty(admin)) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        if (!admin.getStatus() || !ObjectUtils.isEmpty(admin.getDeleteTime())) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "用户已被禁用");
        }
        String code = RandomUtil.randomNumbers(6);
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, phoneParams.getPhone()));
        bucket.set(code, 15L, TimeUnit.MINUTES);
        log.info("短信验证码 : {}", code);
    }

    private Tenant getTenant(String tenantCode) {
        return tenantMapperService.getOne(new LambdaQueryWrapper<Tenant>().eq(Tenant::getCode, tenantCode).isNull(Tenant::getDeleteTime));
    }

    @Override
    public CaptchaResponse getCaptcha() {
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new ClassPathResource("font.ttf").getStream())
                    .deriveFont(Font.PLAIN, 75.0f);
        } catch (Exception e) {
            log.error("字体加载失败.....", e);
            throw new AppException(RCode.DEFAULT);
        }
        lineCaptcha.setFont(font);
        lineCaptcha.createCode();
        String code = lineCaptcha.getCode();
        String key = RandomStringUtils.randomAlphanumeric(12);
        String image = lineCaptcha.getImageBase64Data();
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
        String type = claims.get(AppConst.TOKEN_TYPE, String.class);
        if (!StringUtils.hasText(type) || !AppConst.REFRESH_TOKEN.equalsIgnoreCase(type)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "refresh_token无效");
        }

        // 用户信息
        Long id = claims.get(AppConst.TOKEN_USER_ID, Long.class);
        Long tenantId = claims.get(AppConst.TOKEN_TENANT_ID, Long.class);
        TokenLabelEnum label = TokenLabelEnum.valueOf(claims.get(AppConst.TOKEN_LABEL, String.class));

        switch (label) {
            case ADMIN:
                Admin admin = findByAdminId(id);
                if (ObjectUtils.isEmpty(admin)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }
                List<RoleAdmin> roles = roleAdminMapperService.list(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getAdminId, admin.getId()));
                Assert.notEmpty(roles, "用户角色未分配");

                return getAuthResponse(new UserInfo(id, tenantId, admin.getUsername(), admin.getPhone(),
                        admin.getAvatar(), GenderEnum.UNKNOWN, TokenLabelEnum.ADMIN, roles.stream().map(e -> e.getRoleId()).collect(Collectors.toList())));
            default: // 其它平台
                WechatUser user = findByUserId(id);
                if (ObjectUtils.isEmpty(user)) {
                    throw new AppException(RCode.UNAUTHORIZED_ERROR);
                }
                return getAuthResponse(new UserInfo(id, tenantId, user.getUsername(), user.getPhone(),
                        user.getAvatar(), user.getGender(), TokenLabelEnum.USER, null));
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
        if (!StringUtils.hasText(credentials.getCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, credentials.getPhone()));
        String code = bucket.get();
        if (!StringUtils.hasText(code) || !credentials.getCode().equals(code)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "验证码错误");
        }
        // 验证通过删除code
        bucket.delete();

        Tenant tenant = getTenant(credentials.getTenantCode());

        Assert.notNull(tenant, "租户不存在");

        Admin admin = adminMapperService.getOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getTenantId, tenant.getId())
                .eq(Admin::getPhone, credentials.getPhone())
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime)
        );
        Assert.notNull(admin, RCode.PARAM_ERROR.getMsg());
        List<RoleAdmin> roles = roleAdminMapperService.list(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getAdminId, admin.getId()));
        Assert.notEmpty(roles, "用户角色未分配");

        return getAuthResponse(new UserInfo(admin.getId(), tenant.getId(), admin.getUsername(), admin.getPhone(),
                admin.getAvatar(), GenderEnum.UNKNOWN, TokenLabelEnum.ADMIN, roles.stream().map(i -> i.getRoleId()).collect(Collectors.toList())));
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

        Tenant tenant = getTenant(credentials.getTenantCode());

        Assert.notNull(tenant, "租户不存在");

        // 验证 用户名 密码
        Admin admin = adminMapperService.getOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getTenantId, tenant.getId())
                .eq(Admin::getUsername, credentials.getUsername())
                .eq(Admin::getPassword, DigestUtils.md5Hex(password))
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime)
        );

        Assert.notNull(admin, RCode.PARAM_ERROR.getMsg());
        List<RoleAdmin> roles = roleAdminMapperService.list(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getAdminId, admin.getId()));
        Assert.notEmpty(roles, "用户角色未分配");

        return getAuthResponse(new UserInfo(admin.getId(), tenant.getId(), admin.getUsername(), admin.getPhone(),
                admin.getAvatar(), GenderEnum.UNKNOWN, TokenLabelEnum.ADMIN, roles.stream().map(i -> i.getRoleId()).collect(Collectors.toList())));
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
        accessToken.setLabel(info.getLabel().getValue());
        refreshToken.setLabel(info.getLabel().getValue());

        //  access_token 7200s
        LocalDateTime accessTokenExpire = LocalDateTime.now().plusSeconds(7200L).plusMinutes(5L);
        // refresh_token 180d
        LocalDateTime refreshTokenExpire = LocalDateTime.now().plusDays(180L).plusMinutes(5L);

        String accessTokenStr = JwtUtil.encode(accessTokenExpire, BeanUtil.beanToMap(accessToken, true, true));
        String refreshTokenStr = JwtUtil.encode(refreshTokenExpire, BeanUtil.beanToMap(refreshToken, true, true));

        // response对象
        AuthResponse response = new AuthResponse();

        response.setId(info.getId());
        response.setUsername(info.getUsername());
        response.setPhone(info.getPhone());
        response.setAvatar(info.getAvatar());
        response.setGender(info.getGender().getValue());
        response.setAccessToken(accessTokenStr);
        response.setRefreshToken(refreshTokenStr);

        // 菜单
        if (info.getLabel().equals(TokenLabelEnum.ADMIN)) {
            response.setMenus(getMenu(info));
        }
        return response;
    }

    // 获取菜单信息
    private List<MenuOut> getMenu(UserInfo userInfo) {
        // 用户 权限对应的菜单ids

        List<Long> ids = roleMenuMapperService.list(Wrappers.lambdaQuery(RoleMenu.class).in(RoleMenu::getRoleId, userInfo.getRoleIds()))
                .stream().map(i -> i.getMenuId()).collect(Collectors.toList());
        List<Menu> ms = menuMapperService.list(new QueryWrapper<Menu>()
                .lambda().in(Menu::getId, ids)
                .orderByAsc(Menu::getSort)
        );

        List<MenuOut> out = new ArrayList<>();

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
