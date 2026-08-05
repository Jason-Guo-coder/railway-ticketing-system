package com.gjq.train.business.trainseat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 座位
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
@Getter
@Setter
@TableName("train_seat")
public class TrainSeat {

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
     * 厢序
     */
    @TableField("carriage_index")
    private Integer carriageIndex;

    /**
     * 排号|01, 02
     */
    @TableField("`row`")
    private String row;

    /**
     * 列号|枚举[SeatColEnum]
     */
    @TableField("col")
    private String col;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @TableField("seat_type")
    private String seatType;

    /**
     * 同车厢座序
     */
    @TableField("carriage_seat_index")
    private Integer carriageSeatIndex;

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
