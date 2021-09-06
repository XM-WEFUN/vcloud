# vcloud

Sponsor [![paypal.me/bootvue](https://cdn.jsdelivr.net/gh/boot-vue/pics@main/icon/paypal.svg)](https://www.paypal.me/bootvue)
☕☕☕

## 依赖

[版本依赖](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

|  Spring Cloud   | Spring Cloud Alibaba  | Spring Boot| Nacos | Sentinel| RocketMQ | Seata|
|  ----  | ----  | ----  | ----  | ----  | ----  | ----  |
| 2020.0.3  | 2.2.6.RELEASE |2.4.10 |    1.4.2 |1.8.1 |4.4.0|1.3.0|

## 模块

- auth: 用户认证
- gateway: 网关
- core: 基础配置+redis/spring data数据源等等.....
- web: webmvc相关config
- admin: 后台管理相关接口服务
- scheduler: 任务调度
- mq: 消息服务
- xxx: 其它服务

## docker

```bash
# sentinel组件已经被剔除 需要引入可以自行加入

# sentinel-dashboard image:  registry.cn-shanghai.aliyuncs.com/bootvue/sentinel:latest

docker run -d --name sentinel-dashboard -p 8080:8080 -v /etc/localtime:/etc/localtime registry.cn-shanghai.aliyuncs.com/bootvue/sentinel:latest
```

## spring-fox api

[认证相关 /auth/oauth](http://localhost:8080/auth/swagger-ui/index.html?urls.primaryName=auth)

[管理平台后端接口 /admin](http://localhost:8080/admin/swagger-ui/index.html?urls.primaryName=admin)

## 状态码

```bash
200: success
400: Bad Request(包含各种参数错误)
401: Unauthorized(token无效或refresh token无效)
403: Forbidden没有权限
404: 请求的服务未找到(nacos服务注册好慢)
503: gateway网关异常(sentinel拦截等等)

600: 系统异常 其它杂七杂八异常......
```

## FAQ

- nacos namespace id, JwtUtil key, appconfig appid secret等需要修改

- RSA公钥私钥对, 小程序appid secret要改

- RSA密钥位数: `4096` 密钥格式: `PKCS8`  文本格式: `PEM/Baws64` 填充模式: `pkcs1` 证书密码: `空`

- 前后端 `password` `其它敏感数据...`等信息RSA公钥加密传输

- 客户端/auth/oauth/**下所有接口queryString需要携带对应的`appid` `secret`

- 除了skip-urls 其它接口请求头都要携带token:`access_token`

- access_token: `7200s` ,refresh_token: `expire(180d)`秒 每次与access_token同步刷新, 实际有效时间都会延长5分钟

- gateway向后端服务服务转发请求时 header添加了`id` `username` `openid` `platform` 等....

- 所有用到的cache缓存都要在config.yaml自定义配置中指定 包括 `ttl` `maxIdleTime` 如果没有配置.默认缓存不过期

- nacos config.yml定义了mysql redis sentinel等等配置

- 图形验证码无法生成的 系统需要安装字体库

- 数据库已有表, flyway sql要从>1的version开始 例如:V2

- 弱权限控制, 可以自行完善

## Demo

<table>
    <tr>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/12.png"></td>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/4.png"></td>
    </tr>
     <tr>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vdashboard/next/5.png"></td>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vdashboard/next/10.png"></td>
    </tr>
</table>