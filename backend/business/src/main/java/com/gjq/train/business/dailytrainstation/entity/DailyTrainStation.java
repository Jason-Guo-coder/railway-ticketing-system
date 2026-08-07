package com.gjq.train.business.dailytrainstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 每日车站实体，记录某日某车次经过的车站。
 */
@Getter
@Setter
@TableName("daily_train_station")
public class DailyTrainStation {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("`date`")
    private LocalDate date;

    @TableField("train_code")
    private String trainCode;

    @TableField("`index`")
    private Integer index;

    @TableField("`name`")
    private String name;

    @TableField("name_pinyin")
    private String namePinyin;

    @TableField("in_time")
    private LocalTime inTime;

    @TableField("out_time")
    private LocalTime outTime;

    @TableField("stop_time")
    private LocalTime stopTime;

    @TableField("km")
    private BigDecimal km;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
