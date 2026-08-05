-- Railway ticketing system sample data
-- Run 01_schema.sql before this file.

USE `railway_ticketing_system`;

SET NAMES utf8mb4;

START TRANSACTION;

INSERT INTO `member` (`id`, `mobile`)
VALUES (1, '17714712709'),
       (2, '13800001234');

INSERT INTO `passenger` (
    `id`,
    `member_id`,
    `name`,
    `id_card`,
    `type`,
    `create_time`,
    `update_time`
)
VALUES (1, 1, '张三', '110101199001011234', '1', NOW(3), NOW(3)),
       (2, 2, '李四', '110101199202022345', '2', NOW(3), NOW(3));

INSERT INTO `station` (
    `id`,
    `name`,
    `name_pinyin`,
    `name_py`,
    `create_time`,
    `update_time`
)
VALUES (1, '北京南', 'beijingnan', 'bjn', NOW(3), NOW(3)),
       (2, '上海虹桥', 'shanghaihongqiao', 'shhq', NOW(3), NOW(3));

INSERT INTO `train` (
    `id`,
    `code`,
    `type`,
    `start`,
    `start_pinyin`,
    `start_time`,
    `end`,
    `end_pinyin`,
    `end_time`,
    `create_time`,
    `update_time`
)
VALUES (1, 'G1', 'G', '北京南', 'beijingnan', '08:00:00',
        '上海虹桥', 'shanghaihongqiao', '12:30:00', NOW(3), NOW(3)),
       (2, 'D2', 'D', '上海虹桥', 'shanghaihongqiao', '14:00:00',
        '北京南', 'beijingnan', '19:30:00', NOW(3), NOW(3));

COMMIT;
