-- Railway ticketing system sample data
-- Run 01_schema.sql before this file.

USE `railway_ticketing_system`;

SET NAMES utf8mb4;

START TRANSACTION;

-- 保留课程调试账号，重复执行时更新已有记录。
INSERT INTO `member` (`id`, `mobile`)
VALUES (1, '17714712709'),
       (2, '13800001234')
ON DUPLICATE KEY UPDATE `mobile` = VALUES(`mobile`);

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
       (2, 2, '李四', '110101199202022345', '2', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE `member_id` = VALUES(`member_id`),
                        `name` = VALUES(`name`),
                        `id_card` = VALUES(`id_card`),
                        `type` = VALUES(`type`),
                        `update_time` = NOW(3);

-- 京沪高铁沿线车站。拼音用于后台检索和排序。
INSERT INTO `station` (
    `id`,
    `name`,
    `name_pinyin`,
    `name_py`,
    `create_time`,
    `update_time`
)
VALUES (910000000000001, '北京南', 'beijingnan', 'bjn', NOW(3), NOW(3)),
       (910000000000002, '济南西', 'jinanxi', 'jnx', NOW(3), NOW(3)),
       (910000000000003, '南京南', 'nanjingnan', 'njn', NOW(3), NOW(3)),
       (910000000000004, '上海虹桥', 'shanghaihongqiao', 'shhq', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE `name_pinyin` = VALUES(`name_pinyin`),
                        `name_py` = VALUES(`name_py`),
                        `update_time` = NOW(3);

-- G1/G2 使用京沪高铁代表性时刻，便于演示双向线路。
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
VALUES (910000000000101, 'G1', 'G', '北京南', 'beijingnan', '07:00:00',
        '上海虹桥', 'shanghaihongqiao', '11:32:00', NOW(3), NOW(3)),
       (910000000000102, 'G2', 'G', '上海虹桥', 'shanghaihongqiao', '06:55:00',
        '北京南', 'beijingnan', '11:46:00', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`),
                        `start` = VALUES(`start`),
                        `start_pinyin` = VALUES(`start_pinyin`),
                        `start_time` = VALUES(`start_time`),
                        `end` = VALUES(`end`),
                        `end_pinyin` = VALUES(`end_pinyin`),
                        `end_time` = VALUES(`end_time`),
                        `update_time` = NOW(3);

-- 仅重建本样例拥有的 G1/G2 明细，避免影响其他车次。
DELETE FROM `train_seat` WHERE `train_code` IN ('G1', 'G2');
DELETE FROM `train_carriage` WHERE `train_code` IN ('G1', 'G2');
DELETE FROM `train_station` WHERE `train_code` IN ('G1', 'G2');

INSERT INTO `train_station` (
    `id`,
    `train_code`,
    `index`,
    `name`,
    `name_pinyin`,
    `in_time`,
    `out_time`,
    `stop_time`,
    `km`,
    `create_time`,
    `update_time`
)
VALUES (920000000000101, 'G1', 1, '北京南', 'beijingnan', NULL,
        '07:00:00', NULL, 0.00, NOW(3), NOW(3)),
       (920000000000102, 'G1', 2, '济南西', 'jinanxi', '08:22:00',
        '08:24:00', '00:02:00', 406.00, NOW(3), NOW(3)),
       (920000000000103, 'G1', 3, '南京南', 'nanjingnan', '10:23:00',
        '10:25:00', '00:02:00', 617.00, NOW(3), NOW(3)),
       (920000000000104, 'G1', 4, '上海虹桥', 'shanghaihongqiao',
        '11:32:00', NULL, NULL, 295.00, NOW(3), NOW(3)),
       (920000000000201, 'G2', 1, '上海虹桥', 'shanghaihongqiao', NULL,
        '06:55:00', NULL, 0.00, NOW(3), NOW(3)),
       (920000000000202, 'G2', 2, '南京南', 'nanjingnan', '08:00:00',
        '08:02:00', '00:02:00', 295.00, NOW(3), NOW(3)),
       (920000000000203, 'G2', 3, '济南西', 'jinanxi', '10:02:00',
        '10:04:00', '00:02:00', 617.00, NOW(3), NOW(3)),
       (920000000000204, 'G2', 4, '北京南', 'beijingnan', '11:46:00',
        NULL, NULL, 406.00, NOW(3), NOW(3));

-- 当前学习模型为每趟车准备4节代表车厢：1节一等座、3节二等座。
INSERT INTO `train_carriage` (
    `id`,
    `train_code`,
    `index`,
    `seat_type`,
    `seat_count`,
    `row_count`,
    `col_count`,
    `create_time`,
    `update_time`
)
VALUES (930000000000101, 'G1', 1, '1', 48, 12, 4, NOW(3), NOW(3)),
       (930000000000102, 'G1', 2, '2', 90, 18, 5, NOW(3), NOW(3)),
       (930000000000103, 'G1', 3, '2', 90, 18, 5, NOW(3), NOW(3)),
       (930000000000104, 'G1', 4, '2', 90, 18, 5, NOW(3), NOW(3)),
       (930000000000201, 'G2', 1, '1', 48, 12, 4, NOW(3), NOW(3)),
       (930000000000202, 'G2', 2, '2', 90, 18, 5, NOW(3), NOW(3)),
       (930000000000203, 'G2', 3, '2', 90, 18, 5, NOW(3), NOW(3)),
       (930000000000204, 'G2', 4, '2', 90, 18, 5, NOW(3), NOW(3));

-- 按车厢排数和座位布局生成座位，保证座位总数与车厢seat_count一致。
INSERT INTO `train_seat` (
    `id`,
    `train_code`,
    `carriage_index`,
    `row`,
    `col`,
    `seat_type`,
    `carriage_seat_index`,
    `create_time`,
    `update_time`
)
SELECT 940000000000000
           + IF(carriage.train_code = 'G1', 0, 100000)
           + carriage.`index` * 1000
           + seat_row.row_no * 10
           + IF(
                   carriage.seat_type = '1',
                   seat_col.first_class_index,
                   seat_col.second_class_index
             ),
       carriage.train_code,
       carriage.`index`,
       LPAD(seat_row.row_no, 2, '0'),
       seat_col.col,
       carriage.seat_type,
       (seat_row.row_no - 1) * carriage.col_count
           + IF(
                   carriage.seat_type = '1',
                   seat_col.first_class_index,
                   seat_col.second_class_index
             ),
       NOW(3),
       NOW(3)
FROM `train_carriage` carriage
JOIN (
    SELECT 1 AS row_no UNION ALL SELECT 2 UNION ALL SELECT 3
    UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
    UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
    UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
    UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15
    UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18
) seat_row ON seat_row.row_no <= carriage.row_count
JOIN (
    SELECT 'A' AS col, 1 AS first_class_index, 1 AS second_class_index
    UNION ALL SELECT 'B', NULL, 2
    UNION ALL SELECT 'C', 2, 3
    UNION ALL SELECT 'D', 3, 4
    UNION ALL SELECT 'F', 4, 5
) seat_col ON (carriage.seat_type = '1'
                  AND seat_col.first_class_index IS NOT NULL)
             OR carriage.seat_type = '2'
WHERE carriage.train_code IN ('G1', 'G2');

COMMIT;
