package com.gjq.train.business.dailytrainticket.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 每日余票查询响应。
 */
@Data
public class DailyTrainTicketQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String trainCode;

    private String start;

    private String startPinyin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    private Integer startIndex;

    private String end;

    private String endPinyin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Integer endIndex;

    private Integer ydz;

    private BigDecimal ydzPrice;

    private Integer edz;

    private BigDecimal edzPrice;

    private Integer rw;

    private BigDecimal rwPrice;

    private Integer yw;

    private BigDecimal ywPrice;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
