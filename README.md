# app-cloud

## 依赖

[版本依赖](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

|  Spring Cloud   | Spring Cloud Alibaba  | Spring Boot| Nacos | Sentinel| RocketMQ | Seata|
|  ----  | ----  | ----  | ----  | ----  | ----  | ----  |
| Hoxton.SR9  | 2.2.3.RELEASE |2.3.2.RELEASE |	1.3.3 |1.8.0 |4.4.0|1.3.0|

## 模块

- auth: 用户认证
- gateway: 网关
- core: 基础配置+redis/spring data数据源等等.....
- web: spring-boot-web相关config
- api: api接口
- xxxService: 子服务

## docker

```bash
sentinel-dashboard image:  registry.cn-shanghai.aliyuncs.com/bootvue/sentinel:latest
```

## 说明

1. 所有请求<code>Content-Type: application/json</code>  

2. 文件上传的:  <code>Content-Type: multipart/form-data</code>  

3. 客户端/auth/oauth/**下所有接口queryString需要携带对应的<code>appid</code> <code>secret</code>

4. access_token: 7200s  refresh_token: 7d

5. gateway向服务请求时 header添加了<code>user_id</code> <code>username</code> <code>roles</code> 
 <code>phone</code> <code>avatar</code> <code>tenant_code</code>
 
 6. 所有用到的cache缓存都要在config.yaml自定义配置中指定  包括 <code>ttl</code> <code>maxIdleTime</code> 如果没有配置.默认缓存不过期

```yaml
# 自定义配置
app-cloud:
  swagger: true
  skip-urls:
    - /auth/oauth/**
  auth-key:
    - appid: sysWeb
      secret: 6842224b-7ddb-4c63-af62-1db58d77b2a5
    - appid: app
      secret: a135ec07-6eb2-4300-840a-9977dd8c813c 
  cache:
    - cache-name: xxx  # 实际存储为cache:xxx
      ttl: 1800000      #  毫秒
      max-idle-time: 1200000  #毫秒
```

# 状态码

```bash
200: success
400: Bad Request(包含各种参数错误)
401: Unauthorized(token无效或refresh token无效)
403: Forbidden没有权限

600: 系统异常/其它杂七杂八异常
```