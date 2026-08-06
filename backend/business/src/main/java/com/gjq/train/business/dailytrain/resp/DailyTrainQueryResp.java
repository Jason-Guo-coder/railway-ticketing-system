package com.gjq.train.business.dailytrain.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DailyTrainQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String code;

    private String type;

    private String start;

    private String startPinyin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    private String end;

    private String endPinyin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
