# vcloud

## 依赖

[版本依赖](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

|  Spring Cloud   | Spring Cloud Alibaba  | Spring Boot| Nacos | Sentinel| RocketMQ | Seata|
|  ----  | ----  | ----  | ----  | ----  | ----  | ----  |
| 2020.0.4  | 2.2.6.RELEASE |2.5.5 |    1.4.2 |1.8.1 |4.4.0|1.3.0|

## 模块

> 可以按实际需要更细粒度的拆分模块功能

![arch](https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/arch.svg)

## docker

```bash
# sentinel dashboard

# sentinel-dashboard image:  registry.cn-shanghai.aliyuncs.com/bootvue/sentinel:latest

docker run -d --name sentinel-dashboard \
              -p 8080:8080 \
              -e USERNAME=sentinel \
              -e PASSWORD=sentinel \
              -v /etc/localtime:/etc/localtime \
              registry.cn-shanghai.aliyuncs.com/bootvue/sentinel:latest
```

## swagger文档

在其它服务启动后, 启动`SwaggerApplication`

> http://localhost:8080/swagger/doc.html

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

## next plan

- [ ] 功能完善
- [ ] 说明文档完善

---

## FAQ

- nacos namespace id, JwtUtil key, appconfig appid secret等需要修改

- RSA公钥私钥对, 小程序appid secret要改

- RSA密钥位数: `4096` 密钥格式: `PKCS8`  文本格式: `PEM/Baws64` 填充模式: `pkcs1` 证书密码: `空`

- 前后端 `password` `其它敏感数据...`等信息RSA公钥加密传输

- 客户端/auth/oauth/**下所有接口queryString需要携带对应的`appid` `secret` `platform`参数

- 除了skip-urls 其它接口请求头都要携带token:`access_token`

- access_token: `7200s` ,refresh_token: `expire(180d)`秒 每次与access_token同步刷新, 实际有效时间都会延长5分钟

- gateway向后端服务服务转发请求时 通过`queryString` 传递了`AppUser`对象信息....

- 所有用到的cache缓存都要在config.yaml自定义配置中指定 包括 `ttl` `maxIdleTime` 如果没有配置.默认缓存不过期

- nacos config.yml定义了mysql redis sentinel等等配置

- 数据库已有表, flyway sql要从>1的version开始 例如:V2

- 弱权限控制, 可以自行完善 `PreAuth`

## Contact

![battery_wx](https://cdn.jsdelivr.net/gh/boot-vue/pics@main/wechat.jpg)

## Demo

<table>
    <tr>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/next/1.png"></td>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/next/11.png"></td>
    </tr>
    <tr>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vdashboard/next/22.png"></td>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vdashboard/next/10.png"></td>
    </tr>
</table>
