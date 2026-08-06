package com.gjq.train.business.dailytrain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 每日车次
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-06
 */
@Getter
@Setter
@TableName("daily_train")
public class DailyTrain {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 日期
     */
    @TableField("`date`")
    private LocalDate date;

    /**
     * 车次编号
     */
    @TableField("`code`")
    private String code;

    /**
     * 车次类型|枚举[TrainTypeEnum]
     */
    @TableField("`type`")
    private String type;

    /**
     * 始发站
     */
    @TableField("`start`")
    private String start;

    /**
     * 始发站拼音
     */
    @TableField("start_pinyin")
    private String startPinyin;

    /**
     * 出发时间
     */
    @TableField("start_time")
    private LocalTime startTime;

    /**
     * 终点站
     */
    @TableField("`end`")
    private String end;

    /**
     * 终点站拼音
     */
    @TableField("end_pinyin")
    private String endPinyin;

    /**
     * 到站时间
     */
    @TableField("end_time")
    private LocalTime endTime;

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
