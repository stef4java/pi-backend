-- 创建`spring_ai`数据库
create database `spring_ai` default character set utf8mb4 collate utf8mb4_general_ci;

use `spring_ai`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1、用户信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
create table sys_user
(
    user_id     bigint(20)  not null comment '用户ID',
    user_name   varchar(30) not null comment '用户账号',
    nick_name   varchar(30) not null comment '用户昵称',
    email       varchar(50)  default '' comment '用户邮箱',
    phonenumber varchar(11)  default '' comment '手机号码',
    sex         char(1)      default '0' comment '用户性别（0男 1女 2未知）',
    avatar      bigint(20) comment '头像地址',
    password    varchar(100) default '' comment '密码',
    status      char(1)      default '0' comment '帐号状态（0正常 1停用）',
    del_flag    char(1)      default '0' comment '删除标志（0代表存在 1代表删除）',
    create_by   bigint(20)   default null comment '创建者',
    create_time datetime comment '创建时间',
    update_by   bigint(20)   default null comment '更新者',
    update_time datetime comment '更新时间',
    remark      varchar(500) default null comment '备注',
    primary key (user_id)
) engine = innodb comment = '用户信息表';

BEGIN;
INSERT INTO spring_ai.sys_user (user_id, user_name, nick_name, email, phonenumber, sex, avatar, password, status,
                                del_flag, create_by, create_time, update_by, update_time, remark)
VALUES (1968883485351391234, 'zhangsan', 'zhangsan', '', '', '0', null,
        '$2a$10$U5PoBmcWOpUCHxaZOqRGCumGvFhFbICgaJdA4xudLngmjIT7dOoN6', '0', '0', -1, '2025-09-19 11:42:58', -1,
        '2025-09-19 11:42:58', null);
INSERT INTO spring_ai.sys_user (user_id, user_name, nick_name, email, phonenumber, sex, avatar, password, status,
                                del_flag, create_by, create_time, update_by, update_time, remark)
VALUES (1968883503101685762, 'lisi', 'lisi', '', '', '0', null,
        '$2a$10$t3aEB5o6gtLrLBJz.qqDMOiE0NJoUCUp6j7UPe2y3F5zIk8Nu2EgS', '0', '0', -1, '2025-09-19 11:43:02', -1,
        '2025-09-19 11:43:02', null);
INSERT INTO spring_ai.sys_user (user_id, user_name, nick_name, email, phonenumber, sex, avatar, password, status,
                                del_flag, create_by, create_time, update_by, update_time, remark)
VALUES (1968883523959959554, 'wangwu', 'wangwu', '', '', '0', null,
        '$2a$10$jrmt4wXweHwv/SSK1BDyTueQODrEqKlZXFTKi8cb7ZEBn0hdCOgwS', '0', '0', -1, '2025-09-19 11:43:07', -1,
        '2025-09-19 11:43:07', null);
COMMIT;

-- ----------------------------
-- 订单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_order`;
create table sys_order
(
    id           bigint(20)  not null comment '订单id',
    order_no     varchar(30) not null comment '订单编号',
    user_id      bigint(20)  not null comment '用户id',
    status       tinyint(4) default 0 comment '订单状态（0待支付 1已完成 2已取消）',
    product_name varchar(20) comment '产品名称',
    create_by    bigint(20) comment '创建者',
    create_time  datetime comment '创建时间',
    update_by    bigint(20) comment '更新者',
    update_time  datetime comment '更新时间',
    primary key (id)
) engine = innodb comment = '订单表';


-- ----------------------------
-- 会话表
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
create table conversation
(
    id          bigint(20) not null comment '订单id',
    user_id     bigint(20) not null comment '用户id',
    topic       varchar(20) comment '会话主题',
    create_by   bigint(20) comment '创建者',
    create_time datetime comment '创建时间',
    update_by   bigint(20) comment '更新者',
    update_time datetime comment '更新时间',
    primary key (id)
) engine = innodb comment = '会话表';