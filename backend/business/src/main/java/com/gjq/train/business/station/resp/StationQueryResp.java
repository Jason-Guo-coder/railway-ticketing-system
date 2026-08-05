package com.gjq.train.business.station.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StationQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    private String namePinyin;

    private String namePy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
