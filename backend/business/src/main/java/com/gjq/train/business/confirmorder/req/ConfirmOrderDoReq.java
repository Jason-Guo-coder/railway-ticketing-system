package com.gjq.train.business.confirmorder.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 确认订单提交请求。
 */
@Data
public class ConfirmOrderDoReq {

    @NotNull(message = "日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank(message = "车次编号不能为空")
    private String trainCode;

    @NotBlank(message = "出发站不能为空")
    private String start;

    @NotBlank(message = "到达站不能为空")
    private String end;

    @NotNull(message = "余票ID不能为空")
    private Long dailyTrainTicketId;

    @Valid
    @NotEmpty(message = "车票不能为空")
    private List<ConfirmOrderTicketReq> tickets;
}
