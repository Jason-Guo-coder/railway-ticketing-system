package com.gjq.train.business.traincarriage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 火车车厢
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Getter
@Setter
@TableName("train_carriage")
public class TrainCarriage {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 车次编号
     */
    @TableField("train_code")
    private String trainCode;

    /**
     * 厢号
     */
    @TableField("`index`")
    private Integer index;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @TableField("seat_type")
    private String seatType;

    /**
     * 座位数
     */
    @TableField("seat_count")
    private Integer seatCount;

    /**
     * 排数
     */
    @TableField("`row_count`")
    private Integer rowCount;

    /**
     * 列数
     */
    @TableField("column_count")
    private Integer columnCount;

    /**
     * 新增时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
