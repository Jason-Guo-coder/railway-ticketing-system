package com.gjq.train.business.confirmorder.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 确认订单中的单张车票。
 */
@Data
public class ConfirmOrderTicketReq {

    @NotNull(message = "乘客ID不能为空")
    private Long passengerId;

    @NotBlank(message = "乘客票种不能为空")
    private String passengerType;

    @NotBlank(message = "乘客姓名不能为空")
    private String passengerName;

    @NotBlank(message = "乘客身份证不能为空")
    private String passengerIdCard;

    @NotBlank(message = "座位类型不能为空")
    private String seatTypeCode;

    /**
     * 可选的相对座位，例如A1；为空时由后端分配。
     */
    private String seat;
}
