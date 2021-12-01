/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.100.80
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : 192.168.100.80:3306
 Source Schema         : app

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 25/11/2021 17:14:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dept
-- ----------------------------
DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept`
(
    `id`             bigint UNSIGNED                                              NOT NULL,
    `tenant_id`      bigint                                                       NOT NULL COMMENT '租户id',
    `name`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
    `type`           tinyint(1)                                                   NOT NULL COMMENT '类型 0 公司部门 1 机构/公司 2 小组 3 其它',
    `sort`           int                                                          NOT NULL COMMENT '顺序',
    `p_id`           bigint                                                       NOT NULL COMMENT '上级id 顶级为0',
    `contact_name`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
    `contract_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系方式',
    `remark`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dept
-- ----------------------------

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`
(
    `id`             bigint UNSIGNED                                              NOT NULL,
    `title`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单/按钮名称',
    `key`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一标识',
    `path`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '前端路由',
    `icon`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'icon图标',
    `sort`           int                                                          NOT NULL COMMENT '顺序',
    `p_id`           bigint                                                       NOT NULL COMMENT '上级id  没有上级为0',
    `type`           tinyint(1)                                                   NOT NULL COMMENT '类型 0 菜单 1按钮',
    `action`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限字段',
    `show`           tinyint(1)                                                   NOT NULL COMMENT '菜单栏是否展示 0 不显示 1显示',
    `default_select` tinyint(1)                                                   NOT NULL COMMENT '是否默认选择 0否  1是',
    `default_open`   tinyint(1)                                                   NOT NULL COMMENT '二级菜单是否默认展开  0 否 1是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单/按钮表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu`
VALUES (1464843243430195200, '首页', 'index', '/index', 'HomeOutlined', 0, 0, 0, '', 1, 1, 0);
INSERT INTO `menu`
VALUES (1464843243430195201, '系统管理', 'system', '/system', 'SettingOutlined', 500, 0, 0, '', 1, 0, 1);
INSERT INTO `menu`
VALUES (1464843243430195202, '租户管理', 'tenant', '/tenant', '', 501, 1464843243430195201, 0, 'tenant:list', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195203, '新增', '', '', '', 0, 1464843243430195202, 1, 'tenant:add', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195204, '更新', '', '', '', 0, 1464843243430195202, 1, 'tenant:update', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195205, '删除', '', '', '', 0, 1464843243430195202, 1, 'tenant:delete', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195206, '禁用', '', '', '', 0, 1464843243430195202, 1, 'tenant:disable', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195207, '菜单管理', 'menu', '/menu', '', 502, 1464843243430195201, 0, 'menu:list', 1, 0,
        0);
INSERT INTO `menu`
VALUES (1464843243430195208, '新增', '', '', '', 0, 1464843243430195207, 1, 'menu:add', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195209, '更新', '', '', '', 0, 1464843243430195207, 1, 'menu:update', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195210, '删除', '', '', '', 0, 1464843243430195207, 1, 'menu:delete', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195211, '角色管理', 'role', '/role', '', 503, 1464843243430195201, 0, 'role:list', 1, 0,
        0);
INSERT INTO `menu`
VALUES (1464843243430195212, '新增', '', '', '', 0, 1464843243430195211, 1, 'role:add', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195213, '更新', '', '', '', 0, 1464843243430195211, 1, 'role:update', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195214, '删除', '', '', '', 0, 1464843243430195211, 1, 'role:delete', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195215, '分配用户', '', '', '', 0, 1464843243430195211, 1, 'role:assign', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195216, '部门管理', 'dept', '/dept', '', 504, 1464843243430195201, 0, 'dept:list', 1, 0,
        0);
INSERT INTO `menu`
VALUES (1464843243430195217, '新增', '', '', '', 0, 1464843243430195216, 1, 'dept:add', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195218, '更新', '', '', '', 0, 1464843243430195216, 1, 'dept:update', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195219, '删除', '', '', '', 0, 1464843243430195216, 1, 'dept:delete', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195220, '分配用户', '', '', '', 0, 1464843243430195216, 1, 'dept:assign', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195221, '用户管理', 'user', '/user', '', 505, 1464843243430195201, 0, 'user:list', 1, 0,
        0);
INSERT INTO `menu`
VALUES (1464843243430195222, '新增', '', '', '', 0, 1464843243430195221, 1, 'user:add', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195223, '更新', '', '', '', 0, 1464843243430195221, 1, 'user:update', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195224, '禁用', '', '', '', 0, 1464843243430195221, 1, 'user:disable', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195225, '删除', '', '', '', 0, 1464843243430195221, 1, 'user:delete', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195226, '分配角色', '', '', '', 0, 1464843243430195221, 1, 'user:assign_role', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195227, '分配部门', '', '', '', 0, 1464843243430195221, 1, 'user:assign_dept', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195228, 'Oauth2管理', 'oauth', '/oauth', '', 506, 1464843243430195201, 0,
        'oauth:list',
        1, 0,
        0);
INSERT INTO `menu`
VALUES (1464843243430195229, '新增', '', '', '', 0, 1464843243430195228, 1, 'oauth:add', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195230, '更新', '', '', '', 0, 1464843243430195228, 1, 'oauth:update', 1, 0, 0);
INSERT INTO `menu`
VALUES (1464843243430195231, '删除', '', '', '', 0, 1464843243430195228, 1, 'oauth:delete', 1, 0, 0);

-- ----------------------------
-- Table structure for oauth2_client
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_client`;
CREATE TABLE `oauth2_client`
(
    `id`                   bigint UNSIGNED                                                NOT NULL,
    `tenant_id`            bigint                                                         NOT NULL COMMENT '租户id',
    `client_id`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT 'client id',
    `secret`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT 'secret',
    `grant_type`           varchar(64)                                                    NOT NULL COMMENT 'code,password,refresh_token',
    `scope`                varchar(64)                                                    NOT NULL COMMENT 'all||basic_info',
    `platform`             tinyint(1)                                                     NOT NULL COMMENT '平台类型 0 WEB 1 APP 2 小程序',
    `access_token_expire`  bigint                                                         NOT NULL COMMENT 'access_token 有效时长 s',
    `refresh_token_expire` bigint                                                         NOT NULL COMMENT 'refresh_token 有效时长 s',
    `redirect_url`         varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '重定向url地址',
    `public_key`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          NOT NULL COMMENT '公钥',
    `private_key`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          NOT NULL COMMENT '私钥',
    `create_time`          datetime(3)                                                    NOT NULL COMMENT '创建时间',
    `update_time`          datetime(3)                                                    NULL DEFAULT NULL COMMENT '更新时间',
    `delete_time`          datetime(3)                                                    NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `tc_index` (`tenant_id`, `client_id`) USING BTREE COMMENT '租户id, 客户端id 唯一索引'
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = 'oauth2客户端'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth2_client
-- ----------------------------
insert into oauth2_client
values (1, 1, 'de0b55913ac7', '939da067-8899-40a0-9c21-15f0b0001add', 'code,password,refresh_token', 'all', 0, 7200,
        2592000, '', '-----BEGIN PUBLIC KEY-----
MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA9il0ZDHwNwEGLKjvCYpJ
fjDrM7J2UxXwgwoe+Pgnlvg+jIrXYSe5vwLtrjqJ5M7jlmH4qbG2PYT2WlRkA5Ip
zeOWYph6vAVth7CnHX+Kda/TWJD4dEEcCn+Hk8ulkCf5a0+cBnw/vyJ3+GD8z5f5
ZQJ3YJ0CoPepsdEKRfKQd3eSBGppXz2UmEvCFi10sQGQFOIRcdqL3tUYkJGnJSzZ
oDlqwWyI84mM6JE8NNhSxvZYTy9UQj9ytI3sFhB7jlGigxxVZdayohKQH/FTzi7S
/w1ZDWXitnwhlV5rdIP0/SWedrvebJkdkpvjq0W2MkWo4YFSJHXYtK9pJy1lfxw8
ef3VKf0m86KaoufyOLA/Fljnq3rjRmaOvG9ASJ7EPYKPzSlbqE4nhDH39MBrBc3a
Kbindh8rWi5Gy8JtT0F81//KbG6eH3EgSIuOTIrk/iAmeLTk6uGYhj9csVhneesM
JRCu3JjbRLHzbP0ikrxvbokw/5xsrQOiqFy5UgB/nn8MNyrwKpcdNKeB84b+bTQi
95kqT1tlh/EtYrHW5B8WLXe+/Mfbj4fq8H1rHj1uWbZ3PzewyouhLw+J7F7XueBv
4U0UYaqN/brlXXMxrwS82XfYWbwH3aBsLuye0qAHwiSCXSrRYoE6Gp6sOS0PYcWe
GEXbVfSC09jud0HFx3+RaFUCAwEAAQ==
-----END PUBLIC KEY-----
', '-----BEGIN PRIVATE KEY-----
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
nflau0Dmk4OSv3b6D3qXBYkw6/Lx
-----END PRIVATE KEY-----
', now(3), null, null);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`        bigint UNSIGNED                                              NOT NULL,
    `tenant_id` bigint                                                       NOT NULL COMMENT '租户id',
    `name`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名',
    `action`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限字段',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role`
VALUES (1, 1, '超级管理员', 'admin');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`
(
    `id`      bigint UNSIGNED NOT NULL,
    `role_id` bigint          NOT NULL COMMENT '角色id',
    `menu_id` bigint          NOT NULL COMMENT '菜单id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-菜单表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
insert into `role_menu`
values (1, 1, 1464843243430195200);
insert into `role_menu`
values (2, 1, 1464843243430195201);
insert into `role_menu`
values (3, 1, 1464843243430195202);
insert into `role_menu`
values (4, 1, 1464843243430195203);
insert into `role_menu`
values (5, 1, 1464843243430195204);
insert into `role_menu`
values (6, 1, 1464843243430195205);
insert into `role_menu`
values (7, 1, 1464843243430195206);
insert into `role_menu`
values (8, 1, 1464843243430195207);
insert into `role_menu`
values (9, 1, 1464843243430195208);
insert into `role_menu`
values (10, 1, 1464843243430195209);
insert into `role_menu`
values (11, 1, 1464843243430195210);
insert into `role_menu`
values (12, 1, 1464843243430195211);
insert into `role_menu`
values (13, 1, 1464843243430195212);
insert into `role_menu`
values (14, 1, 1464843243430195213);
insert into `role_menu`
values (15, 1, 1464843243430195214);
insert into `role_menu`
values (16, 1, 1464843243430195215);
insert into `role_menu`
values (17, 1, 1464843243430195216);
insert into `role_menu`
values (18, 1, 1464843243430195217);
insert into `role_menu`
values (19, 1, 1464843243430195218);
insert into `role_menu`
values (20, 1, 1464843243430195219);
insert into `role_menu`
values (21, 1, 1464843243430195220);
insert into `role_menu`
values (22, 1, 1464843243430195221);
insert into `role_menu`
values (23, 1, 1464843243430195222);
insert into `role_menu`
values (24, 1, 1464843243430195223);
insert into `role_menu`
values (25, 1, 1464843243430195224);
insert into `role_menu`
values (26, 1, 1464843243430195225);
insert into `role_menu`
values (27, 1, 1464843243430195226);
insert into `role_menu`
values (28, 1, 1464843243430195227);
insert into `role_menu`
values (29, 1, 1464843243430195228);
insert into `role_menu`
values (30, 1, 1464843243430195229);
insert into `role_menu`
values (31, 1, 1464843243430195230);
insert into `role_menu`
values (32, 1, 1464843243430195231);

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant`
(
    `id`            bigint UNSIGNED                                              NOT NULL,
    `code`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户编号',
    `name`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户名称',
    `contact_name`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
    `contact_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
    `status`        tinyint(1)                                                   NOT NULL COMMENT '状态 0 正常   1 禁用',
    `create_time`   datetime(3)                                                  NOT NULL COMMENT '创建时间',
    `update_time`   datetime(3)                                                  NULL DEFAULT NULL COMMENT '更新时间',
    `delete_time`   datetime(3)                                                  NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `tc_index` (`code`) USING BTREE COMMENT '租户编号 唯一索引'
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '租户表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant
-- ----------------------------
INSERT INTO `tenant`
VALUES (1, 'V0000001', '运营平台', 'admin', '17700000000', 1, now(3), NULL, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`          bigint UNSIGNED                                               NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户id',
    `type`        tinyint(1)                                                    NOT NULL COMMENT '账号类型 0普通用户 1租户管理员 2平台管理员',
    `account`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '账号',
    `password`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '密码',
    `phone`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '手机号',
    `nick_name`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '昵称',
    `gender`      tinyint(1)                                                    NOT NULL COMMENT '性别 1男 2女 0未知',
    `country`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '国家',
    `province`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '省份',
    `city`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '市',
    `avatar`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像',
    `status`      tinyint(1)                                                    NOT NULL COMMENT '状态 0禁用  1正常',
    `create_time` datetime(3)                                                   NOT NULL COMMENT '创建时间',
    `update_time` datetime(3)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    `delete_time` datetime(3)                                                   NULL DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `ta_index` (`tenant_id`, `account`) USING BTREE COMMENT '租户id, 账号 唯一索引'
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user`
VALUES (1, 1, 2, 'admin', md5('123456'), '17700000000', '管理员', 1, '中国', '山东', '济南', '', 1, now(3), NULL,
        NULL);

-- ----------------------------
-- Table structure for user_dept
-- ----------------------------
DROP TABLE IF EXISTS `user_dept`;
CREATE TABLE `user_dept`
(
    `id`      bigint UNSIGNED NOT NULL,
    `user_id` bigint          NOT NULL COMMENT '用户id',
    `dept_id` bigint          NOT NULL COMMENT '部门id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-部门表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_dept
-- ----------------------------

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `id`      bigint UNSIGNED NOT NULL,
    `user_id` bigint          NOT NULL COMMENT '用户id',
    `role_id` bigint          NOT NULL COMMENT '角色id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-角色表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
insert into `user_role`
values (1, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
