package com.gjq.train.business.dailytrainseat.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改每日座位请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DailyTrainSeatUpdateReq extends DailyTrainSeatSaveReq {

    @NotNull(message = "每日座位ID不能为空")
    private Long id;
}
