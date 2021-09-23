/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.100.80-3306
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : 192.168.100.80:3306
 Source Schema         : app

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 11/09/2021 15:50:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户id',
    `username`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '用户名',
    `phone`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '手机号',
    `password`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '密码',
    `avatar`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '头像',
    `status`      tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '账号状态 0:禁用  1:正常',
    `create_time` datetime(3)                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time` datetime(3)                                                   NULL     DEFAULT NULL,
    `delete_time` datetime(3)                                                   NULL     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `tu_index` (`tenant_id`, `username`) USING BTREE,
    INDEX `tp_index` (`tenant_id`, `phone`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT = '管理员用户表';

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin`
VALUES (1436592471676354580, 1436592471676354560, 'admin', '17705920000', md5('123456'), '', 1, now(3), null, null);

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`
(
    `id`             bigint                                                       NOT NULL,
    `title`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
    `sort`           int                                                          NOT NULL COMMENT '菜单顺序',
    `key`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单唯一key',
    `path`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'path',
    `icon`           varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'icon图标',
    `p_id`           bigint                                                       NOT NULL COMMENT '父级菜单id  没有父级为0',
    `show`           tinyint(1)                                                   NOT NULL DEFAULT 1 COMMENT '菜单栏是否展示  0:否  1:是',
    `default_select` tinyint(1)                                                   NOT NULL DEFAULT 0 COMMENT '是否默认选择  0:否   1:是',
    `default_open`   tinyint(1)                                                   NOT NULL DEFAULT 0 COMMENT '二级菜单是否默认展开   0:否  1:是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT = '菜单表';

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu`
VALUES (1436596625371119616, '首页', 0, 'index', '/index', 'HomeOutlined', 0, 1, 1, 0);
INSERT INTO `menu`
VALUES (1436596625371119617, '系统设置', 500, 'setting', '/', 'SettingOutlined', 0, 1, 0, 1);
INSERT INTO `menu`
VALUES (1436596625371119618, '菜单管理', 501, 'menu', '/menu', '', 1436596625371119617, 1, 0, 0);
INSERT INTO `menu`
VALUES (1436596625371119619, '租户管理', 502, 'tenant', '/tenant', '', 1436596625371119617, 1, 0, 0);
INSERT INTO `menu`
VALUES (1436596625371119620, '角色管理', 503, 'role', '/role', '', 1436596625371119617, 1, 0, 0);
INSERT INTO `menu`
VALUES (1436596625371119621, '用户管理', 504, 'admin', '/admin', '', 1436596625371119617, 1, 0, 0);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`        bigint                                                       NOT NULL,
    `tenant_id` bigint                                                       NOT NULL COMMENT '租户id',
    `name`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT = '角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role`
VALUES (1436596625371119621, 1436592471676354560, '超级管理员');

-- ----------------------------
-- Table structure for role_admin
-- ----------------------------
DROP TABLE IF EXISTS `role_admin`;
CREATE TABLE `role_admin`
(
    `id`       bigint NOT NULL,
    `role_id`  bigint NOT NULL COMMENT '角色id',
    `admin_id` bigint NOT NULL COMMENT 'admin用户id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `rm_index` (`role_id`, `admin_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT = '角色-管理员关系表';

-- ----------------------------
-- Records of role_admin
-- ----------------------------
INSERT INTO `role_admin`
VALUES (1436596625371119600, 1436596625371119621, 1436592471676354580);

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`
(
    `id`      bigint NOT NULL,
    `role_id` bigint NOT NULL COMMENT '角色id',
    `menu_id` bigint NOT NULL COMMENT '菜单id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `rm_index` (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT = '角色-菜单关系表';

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu`
VALUES (1436596625371119622, 1436596625371119621, 1436596625371119616);
INSERT INTO `role_menu`
VALUES (1436596625371119623, 1436596625371119621, 1436596625371119617);
INSERT INTO `role_menu`
VALUES (1436596625371119624, 1436596625371119621, 1436596625371119618);
INSERT INTO `role_menu`
VALUES (1436596625371119625, 1436596625371119621, 1436596625371119619);
INSERT INTO `role_menu`
VALUES (1436596625371119626, 1436596625371119621, 1436596625371119620);
INSERT INTO `role_menu`
VALUES (1436596625371119627, 1436596625371119621, 1436596625371119621);

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant`
(
    `id`          bigint                                                       NOT NULL,
    `code`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户编号',
    `name`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户名称',
    `create_time` datetime(3)                                                  NOT NULL,
    `update_time` datetime(3)                                                  NULL DEFAULT NULL,
    `delete_time` datetime(3)                                                  NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `index_code` (`code`) USING BTREE,
    UNIQUE INDEX `index_name` (`name`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic COMMENT = '租户表';

-- ----------------------------
-- Records of tenant
-- ----------------------------
INSERT INTO `tenant`
VALUES (1436592471676354560, 'V0000001', '运营平台', now(3), NULL, NULL);

-- ----------------------------
-- Table structure for wechat_user
-- ----------------------------
DROP TABLE IF EXISTS `wechat_user`;
CREATE TABLE `wechat_user`
(
    `id`          bigint                                                        NOT NULL,
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户id',
    `username`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '用户名 昵称',
    `openid`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '小程序openid',
    `phone`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '手机号',
    `avatar`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '头像',
    `gender`      tinyint                                                       NOT NULL DEFAULT 0 COMMENT '性别 0:未知 1:男 2:女',
    `country`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '国家',
    `province`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '省',
    `city`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '城市',
    `status`      tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '状态 0: 禁用 1: 正常',
    `remark`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '备注, 记录禁用原因等',
    `create_time` datetime(3)                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time` datetime(3)                                                   NULL     DEFAULT NULL,
    `delete_time` datetime(3)                                                   NULL     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `to_index` (`tenant_id`, `openid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT = '微信用户表';

-- ----------------------------
-- Records of wechat_user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
