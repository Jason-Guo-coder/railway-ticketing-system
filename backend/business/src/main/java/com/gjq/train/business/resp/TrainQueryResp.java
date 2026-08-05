package com.gjq.train.business.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TrainQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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
