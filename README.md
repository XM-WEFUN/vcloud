# vcloud

## 依赖

[版本依赖](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

|  Spring Cloud   | Spring Cloud Alibaba  | Spring Boot| Nacos | Sentinel| RocketMQ | Seata|
|  ----  | ----  | ----  | ----  | ----  | ----  | ----  |
| 2020.0.4  | 2.2.6.RELEASE |2.5.5 |    1.4.2 |1.8.1 |4.4.0|1.3.0|

## 模块

```

common          common模块
datasource      数据源
web             spring mvc相关配置
gateway         网关服务                    8080
auth            认证服务                    8081                                
service         服务拆分 (可以自定义)
    ├── admin       后台web端基础服务        8082
    ├── mq          消息中间件服务  
    ├── scheduler   任务调度服务
    ├── swagger     文档服务                9999
    ├── xxx         其它模块               

```

## docker

[sentinel-dashboard](https://hub.docker.com/repository/docker/vbeats/sentinel-dashboard)

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

- [ ] v3版本

---

## FAQ

- nacos namespace id, JwtUtil key,RSA密钥对 ... 等需要修改

- RSA密钥位数: `4096` 密钥格式: `PKCS8`  文本格式: `PEM/Baws64` 填充模式: `pkcs1` 证书密码: `空`

- 前后端 `password` `其它敏感数据...`等信息RSA公钥加密传输

- 除了skip-urls 其它接口请求头都要携带`Authorization: Bearer access_token_value`

- gateway向后端服务服务转发请求时, 会通过`queryString` 传递`AppUser`对象信息....

- 所有用到的cache缓存都要在config.yaml自定义配置中指定 包括 `ttl` `maxIdleTime` 如果没有配置.默认缓存不过期

- nacos config.yml定义了mysql redis sentinel等等配置

- 数据库已有表, flyway sql要从>1的version开始 例如:V2

- 角色权限验证: `@PreAuth(hasRole, hasPermission, superOnly)`

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
