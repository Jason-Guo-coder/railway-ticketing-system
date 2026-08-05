package com.gjq.train.business.trainstation.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class TrainStationSaveReq {

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;

    @NotNull(message = "站序不能为空")
    @Min(value = 1, message = "站序必须大于0")
    private Integer index;

    @NotBlank(message = "站名不能为空")
    @Size(max = 20, message = "站名不能超过20个字符")
    private String name;

    @NotBlank(message = "站名拼音不能为空")
    @Size(max = 50, message = "站名拼音不能超过50个字符")
    private String namePinyin;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime inTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime outTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime stopTime;

    @NotNull(message = "里程不能为空")
    @DecimalMin(value = "0.00", message = "里程不能小于0")
    @Digits(integer = 6, fraction = 2, message = "里程最多保留2位小数")
    private BigDecimal km;
}
