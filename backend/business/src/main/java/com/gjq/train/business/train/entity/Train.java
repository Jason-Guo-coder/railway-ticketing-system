package com.gjq.train.business.train.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@TableName("train")
public class Train {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("code")
    private String code;

    @TableField("type")
    private String type;

    @TableField("`start`")
    private String start;

    @TableField("start_pinyin")
    private String startPinyin;

    @TableField("start_time")
    private LocalTime startTime;

    @TableField("`end`")
    private String end;

    @TableField("end_pinyin")
    private String endPinyin;

    @TableField("end_time")
    private LocalTime endTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
