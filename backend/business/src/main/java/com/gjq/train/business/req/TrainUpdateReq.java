package com.gjq.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TrainUpdateReq {

    @NotNull(message = "车次ID不能为空")
    private Long id;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String code;

    @NotBlank(message = "车次类型不能为空")
    private String type;

    @NotBlank(message = "始发站不能为空")
    @Size(max = 20, message = "始发站不能超过20个字符")
    private String start;

    @NotBlank(message = "始发站拼音不能为空")
    @Size(max = 50, message = "始发站拼音不能超过50个字符")
    private String startPinyin;

    @NotNull(message = "出发时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotBlank(message = "终点站不能为空")
    @Size(max = 20, message = "终点站不能超过20个字符")
    private String end;

    @NotBlank(message = "终点站拼音不能为空")
    @Size(max = 50, message = "终点站拼音不能超过50个字符")
    private String endPinyin;

    @NotNull(message = "到站时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
}
