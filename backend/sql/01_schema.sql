-- Railway ticketing system schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS `railway_ticketing_system`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `railway_ticketing_system`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `train`;
DROP TABLE IF EXISTS `station`;
DROP TABLE IF EXISTS `passenger`;
DROP TABLE IF EXISTS `member`;

CREATE TABLE `member` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `mobile` VARCHAR(11) DEFAULT NULL COMMENT '手机号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `mobile_unique` (`mobile`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '会员';

CREATE TABLE `passenger` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `member_id` BIGINT NOT NULL COMMENT '会员id',
    `name` VARCHAR(20) NOT NULL COMMENT '姓名',
    `id_card` VARCHAR(18) NOT NULL COMMENT '身份证',
    `type` CHAR(1) NOT NULL COMMENT '旅客类型|枚举[PassengerTypeEnum]',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `member_id_index` (`member_id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '乘车人';

CREATE TABLE `station` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `name` VARCHAR(20) NOT NULL COMMENT '站名',
    `name_pinyin` VARCHAR(50) NOT NULL COMMENT '站名拼音',
    `name_py` VARCHAR(50) NOT NULL COMMENT '站名拼音首字母',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name_unique` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '车站';

CREATE TABLE `train` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `type` CHAR(1) NOT NULL COMMENT '车次类型|枚举[TrainTypeEnum]',
    `start` VARCHAR(20) NOT NULL COMMENT '始发站',
    `start_pinyin` VARCHAR(50) NOT NULL COMMENT '始发站拼音',
    `start_time` TIME NOT NULL COMMENT '出发时间',
    `end` VARCHAR(20) NOT NULL COMMENT '终点站',
    `end_pinyin` VARCHAR(50) NOT NULL COMMENT '终点站拼音',
    `end_time` TIME NOT NULL COMMENT '到站时间',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `code_unique` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '车次';

SET FOREIGN_KEY_CHECKS = 1;
