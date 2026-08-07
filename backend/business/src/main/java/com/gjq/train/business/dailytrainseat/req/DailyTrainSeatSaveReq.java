package com.gjq.train.business.dailytrainseat.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新增每日座位请求。
 */
@Data
public class DailyTrainSeatSaveReq {

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;

    @NotNull(message = "厢序不能为空")
    @Min(value = 1, message = "厢序必须大于0")
    private Integer carriageIndex;

    @NotBlank(message = "排号不能为空")
    @Pattern(
            regexp = "^(0[1-9]|[1-9][0-9])$",
            message = "排号必须是01到99之间的两位数字"
    )
    private String row;

    @NotBlank(message = "列号不能为空")
    @Size(max = 1, message = "列号只能包含1个字符")
    private String col;

    @NotBlank(message = "座位类型不能为空")
    private String seatType;

    @NotNull(message = "同车厢座序不能为空")
    @Min(value = 1, message = "同车厢座序必须大于0")
    private Integer carriageSeatIndex;

    @NotBlank(message = "售卖情况不能为空")
    @Size(max = 50, message = "售卖情况不能超过50个车站区间")
    @Pattern(regexp = "^[01]+$", message = "售卖情况只能包含0和1")
    private String sell;
}
