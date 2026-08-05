package com.gjq.train.business.trainstation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 火车车站
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Getter
@Setter
@TableName("train_station")
public class TrainStation {

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
     * 站序
     */
    @TableField("`index`")
    private Integer index;

    /**
     * 站名
     */
    @TableField("`name`")
    private String name;

    /**
     * 站名拼音
     */
    @TableField("name_pinyin")
    private String namePinyin;

    /**
     * 进站时间
     */
    @TableField("in_time")
    private LocalTime inTime;

    /**
     * 出站时间
     */
    @TableField("out_time")
    private LocalTime outTime;

    /**
     * 停站时长
     */
    @TableField("stop_time")
    private LocalTime stopTime;

    /**
     * 里程（公里）|从上一站到本站的距离
     */
    @TableField("km")
    private BigDecimal km;

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
