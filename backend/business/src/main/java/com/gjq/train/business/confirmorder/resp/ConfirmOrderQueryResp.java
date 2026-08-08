package com.gjq.train.business.confirmorder.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 确认订单查询响应。
 */
@Data
public class ConfirmOrderQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long memberId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String trainCode;

    private String start;

    private String end;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long dailyTrainTicketId;

    private String tickets;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
