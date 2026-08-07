package com.gjq.train.business.dailytrainticket.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改每日余票请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DailyTrainTicketUpdateReq extends DailyTrainTicketSaveReq {

    @NotNull(message = "每日余票ID不能为空")
    private Long id;
}
