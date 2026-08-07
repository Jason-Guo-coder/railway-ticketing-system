package com.gjq.train.business.dailytraincarriage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日车厢实体，记录某日某车次的车厢布局。
 */
@Getter
@Setter
@TableName("daily_train_carriage")
public class DailyTrainCarriage {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("`date`")
    private LocalDate date;

    @TableField("train_code")
    private String trainCode;

    @TableField("`index`")
    private Integer index;

    @TableField("seat_type")
    private String seatType;

    @TableField("seat_count")
    private Integer seatCount;

    @TableField("row_count")
    private Integer rowCount;

    @TableField("col_count")
    private Integer colCount;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
