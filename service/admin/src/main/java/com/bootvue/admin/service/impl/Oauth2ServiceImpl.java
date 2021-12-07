package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.service.Oauth2ClientMapperService;
import com.bootvue.admin.service.Oauth2Item;
import com.bootvue.admin.service.Oauth2QueryIn;
import com.bootvue.admin.service.Oauth2Service;
import com.bootvue.common.result.PageOut;
import com.bootvue.datasource.entity.Oauth2Client;
import com.bootvue.datasource.type.PlatformEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Oauth2ServiceImpl implements Oauth2Service {

    private final Oauth2ClientMapperService oauth2ClientMapperService;

    @Override
    public PageOut<List<Oauth2Item>> list(Oauth2QueryIn param) {
        LambdaQueryWrapper<Oauth2Client> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(param.getClientId())) {
            wrapper.eq(Oauth2Client::getClientId, param.getClientId());
        }
        Page<Oauth2Client> pages = oauth2ClientMapperService.page(new Page<>(param.getCurrent(), param.getPageSize()), wrapper);

        PageOut<List<Oauth2Item>> out = new PageOut<>();
        out.setTotal(pages.getTotal());
        out.setRows(pages.getRecords().stream().map(e -> {
            Oauth2Item item = new Oauth2Item();
            BeanUtils.copyProperties(e, item);
            item.setPlatform(e.getPlatform().getValue());
            return item;
        }).collect(Collectors.toList()));
        return out;
    }

    @Override
    public void delete(Long id) {
        Oauth2Client client = oauth2ClientMapperService.getById(id);
        Assert.isTrue(!client.getId().equals(1L), "此client不可删除");
        Assert.notNull(client, "参数错误");
        Assert.isNull(client.getDeleteTime(), "参数错误");
        log.info("删除oauth2 client: {}", client.getClientId());
        client.setDeleteTime(LocalDateTime.now());

        oauth2ClientMapperService.updateById(client);
    }

    @Override
    public void addOrUpdate(Oauth2Item param) {
        Oauth2Client client;
        if (ObjectUtils.isEmpty(param.getId())) {
            // 新增
            Assert.hasText(param.getClientId(), "参数错误");
            Assert.hasText(param.getSecret(), "参数错误");
            Assert.hasText(param.getGrantType(), "参数错误");
            Assert.hasText(param.getScope(), "参数错误");
            Assert.notNull(param.getPlatform(), "参数错误");
            Assert.notNull(param.getAccessTokenExpire(), "参数错误");
            Assert.notNull(param.getRefreshTokenExpire(), "参数错误");
            client = new Oauth2Client();
            client.setCreateTime(LocalDateTime.now());
        } else {
            // 更新
            Assert.isTrue(!param.getId().equals(1L), "此client不可删除");
            client = oauth2ClientMapperService.getById(param.getId());
            Assert.notNull(client, "参数错误");
            Assert.isNull(client.getDeleteTime(), "参数错误");
            client.setUpdateTime(LocalDateTime.now());
        }

        if (StringUtils.hasText(param.getClientId())) {
            client.setClientId(param.getClientId());
        }
        if (StringUtils.hasText(param.getSecret())) {
            client.setSecret(param.getSecret());
        }
        if (StringUtils.hasText(param.getGrantType())) {
            client.setGrantType(param.getGrantType());
        }
        if (StringUtils.hasText(param.getScope())) {
            client.setScope(param.getScope());
        }
        if (!ObjectUtils.isEmpty(param.getPlatform())) {
            client.setPlatform(PlatformEnum.find(param.getPlatform()));
        }
        if (!ObjectUtils.isEmpty(param.getAccessTokenExpire())) {
            client.setAccessTokenExpire(param.getAccessTokenExpire());
        }
        if (!ObjectUtils.isEmpty(param.getRefreshTokenExpire())) {
            client.setRefreshTokenExpire(param.getRefreshTokenExpire());
        }
        client.setRedirectUrl(StringUtils.hasText(param.getRedirectUrl()) ? param.getRedirectUrl() : "");

        oauth2ClientMapperService.saveOrUpdate(client);
    }
}
