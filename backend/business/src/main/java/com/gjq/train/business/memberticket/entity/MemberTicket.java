package com.gjq.train.business.memberticket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员已购买的车票记录。
 */
@Getter
@Setter
@TableName("member_ticket")
public class MemberTicket {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("member_id")
    private Long memberId;

    @TableField("passenger_id")
    private Long passengerId;

    @TableField("`date`")
    private LocalDate date;

    @TableField("train_code")
    private String trainCode;

    @TableField("`start`")
    private String start;

    @TableField("`end`")
    private String end;

    @TableField("carriage_index")
    private Integer carriageIndex;

    @TableField("`row`")
    private String row;

    @TableField("col")
    private String col;

    @TableField("seat_type")
    private String seatType;

    @TableField("seat")
    private String seat;

    @TableField("passenger_type")
    private String passengerType;

    @TableField("passenger_name")
    private String passengerName;

    @TableField("passenger_id_card")
    private String passengerIdCard;

    @TableField("price")
    private BigDecimal price;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
