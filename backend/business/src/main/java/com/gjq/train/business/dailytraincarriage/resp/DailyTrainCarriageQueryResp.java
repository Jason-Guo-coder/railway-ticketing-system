package com.gjq.train.business.dailytraincarriage.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日车厢查询响应。
 */
@Data
public class DailyTrainCarriageQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String trainCode;

    private Integer index;

    private String seatType;

    private Integer seatCount;

    private Integer rowCount;

    private Integer colCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
