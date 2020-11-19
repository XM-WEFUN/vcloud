# app-cloud

## 依赖

[版本依赖](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

|  Spring Cloud   | Spring Cloud Alibaba  | Spring Boot| Nacos | Sentinel| RocketMQ | Seata|
|  ----  | ----  | ----  | ----  | ----  | ----  | ----  |
| Hoxton.SR9  | 2.2.3.RELEASE |2.3.2.RELEASE |	1.3.3 |1.8.0 |4.4.0|1.3.0|

## 模块

- auth: 用户认证
- gateway: 网关
- common: 不包含(spring mvc datasource)
- core: 基础配置+redis/spring data数据源+util等等.....
- generator: mybatis-plus 代码生成器
- api: api接口
- xxxService: 子服务

## 请求方式

所有请求<code>Content-Type: application/json</code>  

文件上传的:  <code>Content-Type: multipart/form-data</code>  

## api文档

[publicApi-开放接口](http://localhost/auth/swagger-ui/?urls.primaryName=publicApi)

[privateApi-需要认证访问的接口](http://localhost/auth/swagger-ui/?urls.primaryName=publicApi)

```
整合gateway后  swagger 接口请求path老是有问题  暂时先这样分开处理吧

http://localhost/auth/swagger-ui/?urls.primaryName=publicApi

http://localhost/api/swagger-ui/?urls.primaryName=privateApi
```
