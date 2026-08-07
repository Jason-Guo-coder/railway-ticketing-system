package com.gjq.train.business.dailytrainseat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日座位实体，包含某日某车次座位的分段售卖状态。
 */
@Getter
@Setter
@TableName("daily_train_seat")
public class DailyTrainSeat {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("`date`")
    private LocalDate date;

    @TableField("train_code")
    private String trainCode;

    @TableField("carriage_index")
    private Integer carriageIndex;

    @TableField("`row`")
    private String row;

    @TableField("col")
    private String col;

    @TableField("seat_type")
    private String seatType;

    @TableField("carriage_seat_index")
    private Integer carriageSeatIndex;

    @TableField("sell")
    private String sell;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
