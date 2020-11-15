# app-cloud

## 模块

- auth: 用户认证
- gateway: 网关
- common: vo/appConfig
- core: 基础配置+redis/spring data数据源+util等等.....
- api: api接口
- xxxService: 子服务


## swagger

[publicApi-开放接口](http://localhost/auth/swagger-ui/?urls.primaryName=publicApi)

[privateApi-需要认证访问的接口](http://localhost/auth/swagger-ui/?urls.primaryName=publicApi)

```
整合gateway后  swagger 接口请求path老是有问题  暂时先这样分开处理吧

http://localhost/auth/swagger-ui/?urls.primaryName=publicApi

http://localhost/api/swagger-ui/?urls.primaryName=privateApi
```
