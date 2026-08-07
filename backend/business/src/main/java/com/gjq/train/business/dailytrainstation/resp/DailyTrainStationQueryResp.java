package com.gjq.train.business.dailytrainstation.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 每日车站查询响应。
 */
@Data
public class DailyTrainStationQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String trainCode;

    private Integer index;

    private String name;

    private String namePinyin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime inTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime outTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime stopTime;

    private BigDecimal km;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
