package com.gjq.train.business.dailytrainticket.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 新增每日余票请求。
 */
@Data
public class DailyTrainTicketSaveReq {

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;

    @NotBlank(message = "出发站不能为空")
    @Size(max = 20, message = "出发站不能超过20个字符")
    private String start;

    @NotBlank(message = "出发站拼音不能为空")
    @Size(max = 50, message = "出发站拼音不能超过50个字符")
    private String startPinyin;

    @NotNull(message = "出发时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "出发站序不能为空")
    @Min(value = 1, message = "出发站序必须大于0")
    private Integer startIndex;

    @NotBlank(message = "到达站不能为空")
    @Size(max = 20, message = "到达站不能超过20个字符")
    private String end;

    @NotBlank(message = "到达站拼音不能为空")
    @Size(max = 50, message = "到达站拼音不能超过50个字符")
    private String endPinyin;

    @NotNull(message = "到站时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotNull(message = "到达站序不能为空")
    @Min(value = 1, message = "到达站序必须大于0")
    private Integer endIndex;

    @NotNull(message = "一等座余票不能为空")
    @Min(value = -1, message = "一等座余票不能小于-1")
    private Integer ydz;

    @NotNull(message = "一等座票价不能为空")
    @DecimalMin(value = "0.00", message = "一等座票价不能小于0")
    @Digits(integer = 6, fraction = 2, message = "一等座票价格式不正确")
    private BigDecimal ydzPrice;

    @NotNull(message = "二等座余票不能为空")
    @Min(value = -1, message = "二等座余票不能小于-1")
    private Integer edz;

    @NotNull(message = "二等座票价不能为空")
    @DecimalMin(value = "0.00", message = "二等座票价不能小于0")
    @Digits(integer = 6, fraction = 2, message = "二等座票价格式不正确")
    private BigDecimal edzPrice;

    @NotNull(message = "软卧余票不能为空")
    @Min(value = -1, message = "软卧余票不能小于-1")
    private Integer rw;

    @NotNull(message = "软卧票价不能为空")
    @DecimalMin(value = "0.00", message = "软卧票价不能小于0")
    @Digits(integer = 6, fraction = 2, message = "软卧票价格式不正确")
    private BigDecimal rwPrice;

    @NotNull(message = "硬卧余票不能为空")
    @Min(value = -1, message = "硬卧余票不能小于-1")
    private Integer yw;

    @NotNull(message = "硬卧票价不能为空")
    @DecimalMin(value = "0.00", message = "硬卧票价不能小于0")
    @Digits(integer = 6, fraction = 2, message = "硬卧票价格式不正确")
    private BigDecimal ywPrice;
}
