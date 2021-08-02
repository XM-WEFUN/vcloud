/*
 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 26/06/2021 16:17:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for action
-- ----------------------------
DROP TABLE IF EXISTS `action`;
CREATE TABLE `action`
(
    `id`     bigint                                                        NOT NULL AUTO_INCREMENT,
    `api`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'api path路径',
    `action` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '按钮:list/update/delete/add',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `api_index` (`api`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 25
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of action
-- ----------------------------
INSERT INTO `action`
VALUES (1, '/admin/index', 'index:list');
INSERT INTO `action`
VALUES (2, '/admin/admin/add', 'admin:add');
INSERT INTO `action`
VALUES (3, '/admin/admin/list', 'admin:list');
INSERT INTO `action`
VALUES (4, '/admin/admin/update', 'admin:update');
INSERT INTO `action`
VALUES (5, '/admin/admin/update_status', 'admin:update');
INSERT INTO `action`
VALUES (6, '/admin/admin/delete', 'admin:delete');
INSERT INTO `action`
VALUES (7, '/admin/role/add', 'role:add');
INSERT INTO `action`
VALUES (8, '/admin/role/list', 'role:list');
INSERT INTO `action`
VALUES (9, '/admin/role/update', 'role:update');
INSERT INTO `action`
VALUES (10, '/admin/role/delete', 'role:delete');
INSERT INTO `action`
VALUES (11, '/admin/action/list', 'action:list');
INSERT INTO `action`
VALUES (12, '/admin/action/update', 'action:update');
INSERT INTO `action`
VALUES (13, '/admin/admin/list_by_role', 'admin:list');
INSERT INTO `action`
VALUES (14, '/admin/admin/update_roles', 'admin:update');
INSERT INTO `action`
VALUES (15, '/admin/tenant/list', 'tenant:list');
INSERT INTO `action`
VALUES (16, '/admin/tenant/update', 'tenant:update');
INSERT INTO `action`
VALUES (17, '/admin/tenant/add', 'tenant:add');
INSERT INTO `action`
VALUES (18, '/admin/tenant/delete', 'tenant:delete');
INSERT INTO `action`
VALUES (19, '/admin/menu/add', 'menu:add');
INSERT INTO `action`
VALUES (20, '/admin/menu/list', 'menu:list');
INSERT INTO `action`
VALUES (21, '/admin/menu/update', 'menu:update');
INSERT INTO `action`
VALUES (22, '/admin/menu/delete', 'menu:delete');
INSERT INTO `action`
VALUES (23, '/admin/action/add', 'action:add');
INSERT INTO `action`
VALUES (24, '/admin/action/delete', 'action:delete');
INSERT INTO `action`
VALUES (25, '/admin/action/list_item', 'action:list');
INSERT INTO `action`
VALUES (26, '/admin/action/update_item', 'action:update');
INSERT INTO `action`
VALUES (27, '/admin/menu/list_by_tenant', 'menu:list');

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
    `role_id`     bigint                                                        NOT NULL DEFAULT 0 COMMENT '角色id',
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
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin`
VALUES (1, 'admin', '17705920000', md5('123456'), 1, 1, '', 1, now(3), null, NULL);

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`
(
    `id`             bigint                                                         NOT NULL AUTO_INCREMENT,
    `tenant_id`      bigint                                                         NOT NULL COMMENT 'tenant id',
    `title`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT '菜单名称',
    `sort`           int                                                            NOT NULL COMMENT '菜单顺序',
    `key`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT '菜单唯一key',
    `path`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL COMMENT 'path',
    `icon`           varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   NOT NULL DEFAULT '' COMMENT 'icon图标',
    `p_id`           bigint                                                         NOT NULL COMMENT '父级菜单id  没有父级为0',
    `actions`        varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '菜单对应的action权限',
    `default_select` tinyint(1)                                                     NOT NULL DEFAULT 0 COMMENT '是否默认选择  0:否   1:是',
    `default_open`   tinyint(1)                                                     NOT NULL DEFAULT 0 COMMENT '二级菜单是否默认展开   0:否  1:是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu`
VALUES (1, 1, '首页', 0, 'index', '/index', 'HomeOutlined', 0, '[{\"label\":\"查看\",\"value\":\"index:list\"}]', 1, 0);
INSERT INTO `menu`
VALUES (2, 1, '系统设置', 100, 'setting', '/', 'SettingOutlined', 0, '[{\"label\":\"查看\",\"value\":\"list\"}]', 0, 1);
INSERT INTO `menu`
VALUES (3, 1, '租户管理', 101, 'tenant', '/tenant', '', 2,
        '[{\"label\":\"查看\",\"value\":\"tenant:list\"},{\"label\":\"新增\",\"value\":\"tenant:add\"},{\"label\":\"修改\",\"value\":\"tenant:update\"},{\"label\":\"删除\",\"value\":\"tenant:delete\"}]',
        0, 0);
INSERT INTO `menu`
VALUES (4, 1, '菜单管理', 102, 'menu', '/menu', '', 2,
        '[{\"label\":\"查看\",\"value\":\"menu:list\"},{\"label\":\"新增\",\"value\":\"menu:add,tenant:list\"},{\"label\":\"修改\",\"value\":\"menu:update\"},{\"label\":\"删除\",\"value\":\"menu:delete\"}]',
        0, 0);
INSERT INTO `menu`
VALUES (5, 1, '接口权限', 103, 'action', '/action', '', 2,
        '[{\"label\":\"查看\",\"value\":\"action:list\"},{\"label\":\"新增\",\"value\":\"action:add\"},{\"label\":\"修改\",\"value\":\"action:update\"},{\"label\":\"删除\",\"value\":\"action:delete\"}]',
        0, 0);
INSERT INTO `menu`
VALUES (6, 1, '角色管理', 104, 'role', '/role', '', 2,
        '[{\"label\":\"查看\",\"value\":\"role:list\"},{\"label\":\"修改\",\"value\":\"role:update\"},{\"label\":\"新增\",\"value\":\"role:add\"},{\"label\":\"删除\",\"value\":\"role:delete\"},{\"label\":\"权限\",\"value\":\"action:list,action:update\"},{\"label\":\"用户\",\"value\":\"admin:list,admin:update\"}]',
        0, 0);
INSERT INTO `menu`
VALUES (7, 1, '用户管理', 105, 'admin', '/admin', '', 2,
        '[{\"label\":\"查看\",\"value\":\"admin:list\"},{\"label\":\"修改\",\"value\":\"admin:update\"},{\"label\":\"新增\",\"value\":\"admin:add\"},{\"label\":\"删除\",\"value\":\"admin:delete\"}]',
        0, 0);

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
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role`
VALUES (1, 1, '超级管理员');

-- ----------------------------
-- Table structure for role_menu_action
-- ----------------------------
DROP TABLE IF EXISTS `role_menu_action`;
CREATE TABLE `role_menu_action`
(
    `id`         bigint                                                         NOT NULL AUTO_INCREMENT,
    `role_id`    bigint                                                         NOT NULL COMMENT '角色id',
    `menu_id`    bigint                                                         NOT NULL COMMENT 'menu id',
    `action_ids` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'action id ,分隔',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_menu_action
-- ----------------------------
INSERT INTO `role_menu_action`
VALUES (1, 1, 1, '1');
INSERT INTO `role_menu_action`
VALUES (2, 1, 2, '0');
INSERT INTO `role_menu_action`
VALUES (3, 1, 3, '15,16,17,18');
INSERT INTO `role_menu_action`
VALUES (4, 1, 4, '15,19,20,21,22,27');
INSERT INTO `role_menu_action`
VALUES (5, 1, 5, '23,24,25,26');
INSERT INTO `role_menu_action`
VALUES (6, 1, 6, '3,4,7,8,9,10,11,12,13,14,15');
INSERT INTO `role_menu_action`
VALUES (7, 1, 7, '2,3,4,5,6,8,9,15');

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
  AUTO_INCREMENT = 2
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
    `gender`      tinyint(4)                                                    NOT NULL DEFAULT 0 COMMENT '性别 0:未知 1:男 2:女',
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
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
