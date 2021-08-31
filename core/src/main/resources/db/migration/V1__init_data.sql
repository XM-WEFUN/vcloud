/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.100.80
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : 192.168.100.80:3306
 Source Schema         : app

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 30/08/2021 17:57:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT,
    `username`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '用户名',
    `phone`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '手机号',
    `password`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '密码',
    `tenant_id`   bigint                                                        NOT NULL COMMENT '租户id',
    `role_ids`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色id集合',
    `avatar`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '头像',
    `status`      tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '账号状态 0:禁用  1:正常',
    `create_time` datetime(3)                                                   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `update_time` datetime(3)                                                   NULL     DEFAULT NULL,
    `delete_time` datetime(3)                                                   NULL     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `ut_index` (`username`, `tenant_id`) USING BTREE,
    INDEX `username_index` (`username`) USING BTREE,
    INDEX `phone_index` (`phone`) USING BTREE,
    INDEX `tenant_index` (`tenant_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin`
VALUES (1, 'admin', '17705920000', md5('123456'), 1, '1', '', 1, now(3), NULL,
        NULL);

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`
(
    `id`             bigint                                                       NOT NULL AUTO_INCREMENT,
    `tenant_id`      bigint                                                       NOT NULL COMMENT 'tenant id',
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
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu`
VALUES (1, 1, '首页', 0, 'index', '/index', 'HomeOutlined', 0, 1, 1, 0);
INSERT INTO `menu`
VALUES (2, 1, '系统设置', 500, 'setting', '/setting', 'SettingOutlined', 0, 1, 0, 1);
INSERT INTO `menu`
VALUES (3, 1, '租户管理', 501, 'tenant', '/tenant', '', 2, 1, 0, 0);
INSERT INTO `menu`
VALUES (4, 1, '菜单管理', 502, 'menu', '/menu', '', 2, 1, 0, 0);
INSERT INTO `menu`
VALUES (5, 1, '角色管理', 503, 'role', '/role', '', 2, 1, 0, 0);
INSERT INTO `menu`
VALUES (6, 1, '用户管理', 504, 'admin', '/admin', '', 2, 1, 0, 0);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`        bigint                                                       NOT NULL AUTO_INCREMENT,
    `tenant_id` bigint                                                       NOT NULL COMMENT '租户id',
    `name`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `tenant_index` (`tenant_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role`
VALUES (1, 1, '超级管理员');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`
(
    `id`      bigint NOT NULL AUTO_INCREMENT,
    `role_id` bigint NOT NULL COMMENT '角色id',
    `menu_id` bigint NOT NULL COMMENT 'menu id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu`
VALUES (1, 1, 1);
INSERT INTO `role_menu`
VALUES (2, 1, 2);
INSERT INTO `role_menu`
VALUES (3, 1, 3);
INSERT INTO `role_menu`
VALUES (4, 1, 4);
INSERT INTO `role_menu`
VALUES (5, 1, 5);
INSERT INTO `role_menu`
VALUES (6, 1, 6);

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant`
(
    `id`          bigint                                                       NOT NULL AUTO_INCREMENT,
    `code`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '编号',
    `name`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户名称',
    `create_time` datetime(3)                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `delete_time` datetime(3)                                                  NULL     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `code_index` (`code`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant
-- ----------------------------
INSERT INTO `tenant`
VALUES (1, 'V00000000001', '运营平台', now(3), NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT,
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
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `openid_index` (`tenant_id`, `openid`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
