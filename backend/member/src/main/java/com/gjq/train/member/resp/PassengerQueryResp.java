package com.gjq.train.member.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 乘车人列表响应。
 */
@Data
public class PassengerQueryResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long memberId;

    private String name;

    private String idCard;

    private String type;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
