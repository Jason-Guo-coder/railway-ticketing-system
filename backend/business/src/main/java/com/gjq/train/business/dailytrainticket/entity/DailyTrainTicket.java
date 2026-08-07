package com.gjq.train.business.dailytrainticket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 余票信息
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-07
 */
@Getter
@Setter
@TableName("daily_train_ticket")
public class DailyTrainTicket {

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
    @TableField("train_code")
    private String trainCode;

    /**
     * 出发站
     */
    @TableField("`start`")
    private String start;

    /**
     * 出发站拼音
     */
    @TableField("start_pinyin")
    private String startPinyin;

    /**
     * 出发时间
     */
    @TableField("start_time")
    private LocalTime startTime;

    /**
     * 出发站序|本站是整个车次的第几站
     */
    @TableField("start_index")
    private Integer startIndex;

    /**
     * 到达站
     */
    @TableField("`end`")
    private String end;

    /**
     * 到达站拼音
     */
    @TableField("end_pinyin")
    private String endPinyin;

    /**
     * 到站时间
     */
    @TableField("end_time")
    private LocalTime endTime;

    /**
     * 到站站序|本站是整个车次的第几站
     */
    @TableField("end_index")
    private Integer endIndex;

    /**
     * 一等座余票
     */
    @TableField("ydz")
    private Integer ydz;

    /**
     * 一等座票价
     */
    @TableField("ydz_price")
    private BigDecimal ydzPrice;

    /**
     * 二等座余票
     */
    @TableField("edz")
    private Integer edz;

    /**
     * 二等座票价
     */
    @TableField("edz_price")
    private BigDecimal edzPrice;

    /**
     * 软卧余票
     */
    @TableField("rw")
    private Integer rw;

    /**
     * 软卧票价
     */
    @TableField("rw_price")
    private BigDecimal rwPrice;

    /**
     * 硬卧余票
     */
    @TableField("yw")
    private Integer yw;

    /**
     * 硬卧票价
     */
    @TableField("yw_price")
    private BigDecimal ywPrice;

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
