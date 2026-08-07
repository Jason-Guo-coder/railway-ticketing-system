package com.gjq.train.business.dailytraincarriage.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改每日车厢请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DailyTrainCarriageUpdateReq
        extends DailyTrainCarriageSaveReq {

    @NotNull(message = "每日车厢ID不能为空")
    private Long id;
}
