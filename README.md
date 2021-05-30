# vcloud

Sponsor [![paypal.me/bootvue](https://cdn.jsdelivr.net/gh/boot-vue/pics@main/icon/paypal.svg)](https://www.paypal.me/bootvue)
☕☕☕

## 依赖

[版本依赖](https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E)

|  Spring Cloud   | Spring Cloud Alibaba  | Spring Boot| Nacos | Sentinel| RocketMQ | Seata|
|  ----  | ----  | ----  | ----  | ----  | ----  | ----  |
| 2020.0.2  | 2021.1 |2.4.2 |    1.4.1 |1.8.0 |4.4.0|1.3.0|

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

## 认证

`type` : [必需] 0: 换取新的access_token与refresh_token 1: 用户名密码登录 2: 短信登录 3: 微信小程序认证

`platform` : [必需] 客户端平台类型 0:web 1:微信小程序 2:android 3:ios

- 用户名密码登录

  ```json
  {
    "tenant_code": "000000",
    "username": "test",
    "password": "123456",
    "key": "HQQks32jqgvY",
    "code": "218176",
    "type": 1,
    "platform": 0
  }
  ```
- 短信登录

  ```json
  {
    "tenant_code": "000000",
    "code": "218176",
    "phone": "17705920001",
    "type": 2,
    "platform": 0
  }
  ```

- 换取新的access_token

  ```json
  {
    "type": 0,
    "platform": 0,
    "refresh_token": "xxxxxx"
  }
  ```

- 微信小程序

  ```json
  {
    "tenant_code": "000000",
    "type": 3,
    "platform": 1,
    "wechat": {
        "code": "xxx",
        "nick_name": "",
        "gender": 1,
        "avatar_url": "",
        "province": "",
        "country": "",
        "city": "",
        "iv": "",
        "encrypted_data": "",
        "raw_data": "",
        "signature": ""
     }
  }
  ```

- 认证接口响应数据

  ```json
  {
    "code": 200,
    "msg": "success",
    "data": {
        "user_id": 1,
        "tenant_code": "000000",
        "username": "admin",
        "nickname": "",
        "phone": "17705920000",
        "avatar": "",
        "roles": "admin",
        "access_token": "xxoo",
        "refresh_token": "ooxx",
        "expires": 7200
    }
  }
  ```

- token结构

  `access_token`: `7200s` `refresh_token`: `20d`

  ```json
  {
  "user_id": 1,
  "username": "admin",
  "role_id": 0,
  "type": "access_token",
  "jti": "cfd22c99-e0b8-4e98-a8af-xxoo",
  "iat": 1610961974,
  "sub": "vcloud",
  "exp": 1610969174
  }
  ```

## 菜单权限

> 一个用户只能属于一个role, 我定的, 咋的了

> permissions 字段格式 ["按钮:add,list,update,delete"]或["list"] (菜单是否可以查看)

> 按钮非特指, 对应前端当前页面下同类型的所有按钮, 需要前后端约定好

- `role_id`: >0 有效的管理平台用户(系统管理员或代理平台等) 0(初始用户(管理平台没有菜单权限的用户/非系统、代理等管理平台端的用户))   -1(普通用户, 无平台权限)

- 二级菜单父一级 `permissions` ["list"]或者没有, 对应 `role_menu_action actions`字段, `0`(有查看权限)/`-1`(没有查看权限)

```json
{
  "menus": [
    {
      "key": "index",
      "icon": "HomeOutlined",
      "title": "首页",
      "default_select": true,
      "default_open": false,
      "permissions": [
        "index:list"
      ]
    },
    {
      "key": "setting",
      "icon": "SettingOutlined",
      "title": "系统设置",
      "default_select": false,
      "default_open": true,
      "permissions": [
        "list"
      ],
      "children": [
        {
          "key": "user",
          "title": "用户管理",
          "default_select": false,
          "default_open": false,
          "permissions": [
            "role:update,list",
            "user:add,update,list,delete"
          ]
        },
        {
          "key": "role",
          "title": "角色管理",
          "default_select": false,
          "default_open": false,
          "permissions": [
            "action:update,list",
            "role:add,update,list,delete"
          ]
        }
      ]
    }
  ]
}
```

## FAQ

- 所有post请求 `Content-Type: application/json`

- 文件上传的:  `Content-Type: multipart/form-data`

- nacos namespace id, JwtUtil key, appconfig appid secret需要修改

- RSA公钥私钥对, 小程序appid secret要改

- RSA密钥位数: `4096` 密钥格式: `PKCS8`  文本格式: `PEM/Baws64` 填充模式: `pkcs1` 证书密码: `空`

- 前后端 `password` `其它敏感数据...`等信息RSA公钥加密传输

- 客户端/auth/oauth/**下所有接口queryString需要携带对应的`appid` `secret`

- 除了skip-urls 其它接口请求头都要携带token:`access_token`

- access_token: `7200s` refresh_token: `20d` 每次与access_token同步刷新

- gateway向后端服务服务转发请求时 header添加了`user_id` `username` `role_id` `openid` `tenant_id`

- 所有用到的cache缓存都要在config.yaml自定义配置中指定 包括 `ttl` `maxIdleTime` 如果没有配置.默认缓存不过期

- nacos config.yml定义了mysql redis sentinel等等配置

- 图形验证码无法生成的 系统需要安装字体库

- 数据库已有表, flyway sql要从>1的version开始 例如:V2

- 数据初始化: 每个租户`role`表至少有一个超级管理员的角色,
  `menu`表填充所有的菜单信息,`actions`字段为菜单对应的action权限, 格式json字符串(二级菜单父一级的value为`list`):
  ```json
  [{"label":"查看","value":"role:list"},{"label":"修改","value":"role:update"},{"label":"新增","value":"role:add"},{"label":"删除","value":"role:delete"},{"label":"权限","value":"action:list,update"},{"label":"用户","value":"user:list,update"}]
  ```
  `action`填写所有需要做权限控制的接口信息,`role_menu_action`初始分配租户超级管理员的菜单接口对应关系

```yaml
# 自定义配置
vcloud:
  swagger: true
  skip-urls:
    - /auth/oauth/**
  auth-key:
    - appid: sysWeb
      secret: 6842224b-7ddb-4c63-af62-1db58d77b2a5
      platform: 1
      public-key: "ooo"
      private-key: "xxxx"
    - appid: app
      secret: a135ec07-6eb2-4300-840a-9977dd8c813c
      platform: 2
      public-key: "oooooooo"
      private-key: "xxxx"
      wechat-appid: "ccc"
      wechat-secret: "dddd"
  cache:
    - cache-name: xxx  # 实际存储为cache:xxx
      ttl: 1800000      #  毫秒
      max-idle-time: 1200000  #毫秒
```

## Demo

<table>
    <tr>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/3.png"></td>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/4.png"></td>
    </tr>
     <tr>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/1.png"></td>
        <td><img src="https://cdn.jsdelivr.net/gh/boot-vue/pics@main/vcloud/2.png"></td>
    </tr>
</table>