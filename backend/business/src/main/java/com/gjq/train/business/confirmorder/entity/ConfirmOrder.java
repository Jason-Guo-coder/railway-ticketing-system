package com.gjq.train.business.confirmorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 确认订单
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-08
 */
@Getter
@Setter
@TableName("confirm_order")
public class ConfirmOrder {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会员id
     */
    @TableField("member_id")
    private Long memberId;

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
     * 到达站
     */
    @TableField("`end`")
    private String end;

    /**
     * 余票ID
     */
    @TableField("daily_train_ticket_id")
    private Long dailyTrainTicketId;

    /**
     * 车票
     */
    @TableField("tickets")
    private String tickets;

    /**
     * 订单状态|枚举[ConfirmOrderStatusEnum]
     */
    @TableField("`status`")
    private String status;

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
