package com.gjq.train.business.dailytrainseat.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日座位查询响应。
 */
@Data
public class DailyTrainSeatQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String trainCode;

    private Integer carriageIndex;

    private String row;

    private String col;

    private String seatType;

    private Integer carriageSeatIndex;

    private String sell;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
