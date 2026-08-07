-- Railway ticketing system schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS `railway_ticketing_system`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `railway_ticketing_system`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `daily_train_ticket`;
DROP TABLE IF EXISTS `daily_train_seat`;
DROP TABLE IF EXISTS `daily_train_carriage`;
DROP TABLE IF EXISTS `daily_train_station`;
DROP TABLE IF EXISTS `daily_train`;
DROP TABLE IF EXISTS `train_seat`;
DROP TABLE IF EXISTS `train_carriage`;
DROP TABLE IF EXISTS `train_station`;
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

CREATE TABLE `train_station` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `index` INT NOT NULL COMMENT '站序',
    `name` VARCHAR(20) NOT NULL COMMENT '站名',
    `name_pinyin` VARCHAR(50) NOT NULL COMMENT '站名拼音',
    `in_time` TIME DEFAULT NULL COMMENT '进站时间',
    `out_time` TIME DEFAULT NULL COMMENT '出站时间',
    `stop_time` TIME DEFAULT NULL COMMENT '停站时长',
    `km` DECIMAL(8, 2) NOT NULL COMMENT '里程（公里）|从上一站到本站的距离',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `train_code_index_unique` (`train_code`, `index`),
    UNIQUE KEY `train_code_name_unique` (`train_code`, `name`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '火车车站';

CREATE TABLE `train_carriage` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `index` INT NOT NULL COMMENT '厢号',
    `seat_type` CHAR(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
    `seat_count` INT NOT NULL COMMENT '座位数',
    `row_count` INT NOT NULL COMMENT '排数',
    `col_count` INT NOT NULL COMMENT '列数',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `train_code_index_unique` (`train_code`, `index`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '火车车厢';

CREATE TABLE `train_seat` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `carriage_index` INT NOT NULL COMMENT '厢序',
    `row` CHAR(2) NOT NULL COMMENT '排号|01, 02',
    `col` CHAR(1) NOT NULL COMMENT '列号|枚举[SeatColEnum]',
    `seat_type` CHAR(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
    `carriage_seat_index` INT NOT NULL COMMENT '同车厢座序',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '座位';

CREATE TABLE `daily_train` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `date` DATE NOT NULL COMMENT '日期',
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
    UNIQUE KEY `date_code_unique` (`date`, `code`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '每日车次';

CREATE TABLE `daily_train_station` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `date` DATE NOT NULL COMMENT '日期',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `index` INT NOT NULL COMMENT '站序',
    `name` VARCHAR(20) NOT NULL COMMENT '站名',
    `name_pinyin` VARCHAR(50) NOT NULL COMMENT '站名拼音',
    `in_time` TIME DEFAULT NULL COMMENT '进站时间',
    `out_time` TIME DEFAULT NULL COMMENT '出站时间',
    `stop_time` TIME DEFAULT NULL COMMENT '停站时长',
    `km` DECIMAL(8, 2) NOT NULL COMMENT '里程（公里）|从上一站到本站的距离',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `date_train_code_index_unique` (`date`, `train_code`, `index`),
    UNIQUE KEY `date_train_code_name_unique` (`date`, `train_code`, `name`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '每日车站';

CREATE TABLE `daily_train_carriage` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `date` DATE NOT NULL COMMENT '日期',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `index` INT NOT NULL COMMENT '厢序',
    `seat_type` CHAR(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
    `seat_count` INT NOT NULL COMMENT '座位数',
    `row_count` INT NOT NULL COMMENT '排数',
    `col_count` INT NOT NULL COMMENT '列数',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `date_train_code_index_unique`
        (`date`, `train_code`, `index`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '每日车厢';

CREATE TABLE `daily_train_seat` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `date` DATE NOT NULL COMMENT '日期',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `carriage_index` INT NOT NULL COMMENT '厢序',
    `row` CHAR(2) NOT NULL COMMENT '排号|01, 02',
    `col` CHAR(1) NOT NULL COMMENT '列号|枚举[SeatColEnum]',
    `seat_type` CHAR(1) NOT NULL COMMENT '座位类型|枚举[SeatTypeEnum]',
    `carriage_seat_index` INT NOT NULL COMMENT '同车厢座序',
    `sell` VARCHAR(50) NOT NULL COMMENT '售卖情况|将经过的车站用01拼接，0表示可卖，1表示已卖',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '每日座位';

CREATE TABLE `daily_train_ticket` (
    `id` BIGINT NOT NULL COMMENT 'id',
    `date` DATE NOT NULL COMMENT '日期',
    `train_code` VARCHAR(20) NOT NULL COMMENT '车次编号',
    `start` VARCHAR(20) NOT NULL COMMENT '出发站',
    `start_pinyin` VARCHAR(50) NOT NULL COMMENT '出发站拼音',
    `start_time` TIME NOT NULL COMMENT '出发时间',
    `start_index` INT NOT NULL COMMENT '出发站序|本站是整个车次的第几站',
    `end` VARCHAR(20) NOT NULL COMMENT '到达站',
    `end_pinyin` VARCHAR(50) NOT NULL COMMENT '到达站拼音',
    `end_time` TIME NOT NULL COMMENT '到站时间',
    `end_index` INT NOT NULL COMMENT '到站站序|本站是整个车次的第几站',
    `ydz` INT NOT NULL COMMENT '一等座余票',
    `ydz_price` DECIMAL(8, 2) NOT NULL COMMENT '一等座票价',
    `edz` INT NOT NULL COMMENT '二等座余票',
    `edz_price` DECIMAL(8, 2) NOT NULL COMMENT '二等座票价',
    `rw` INT NOT NULL COMMENT '软卧余票',
    `rw_price` DECIMAL(8, 2) NOT NULL COMMENT '软卧票价',
    `yw` INT NOT NULL COMMENT '硬卧余票',
    `yw_price` DECIMAL(8, 2) NOT NULL COMMENT '硬卧票价',
    `create_time` DATETIME(3) DEFAULT NULL COMMENT '新增时间',
    `update_time` DATETIME(3) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `date_train_code_start_end_unique`
        (`date`, `train_code`, `start`, `end`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '余票信息';

SET FOREIGN_KEY_CHECKS = 1;
