package com.gjq.train.business.dailytraincarriage.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新增每日车厢请求。
 */
@Data
public class DailyTrainCarriageSaveReq {

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;

    @NotNull(message = "厢序不能为空")
    @Min(value = 1, message = "厢序必须大于0")
    private Integer index;

    @NotBlank(message = "座位类型不能为空")
    private String seatType;

    @NotNull(message = "排数不能为空")
    @Min(value = 1, message = "排数必须大于0")
    @Max(value = 99, message = "排数不能超过99")
    private Integer rowCount;
}
