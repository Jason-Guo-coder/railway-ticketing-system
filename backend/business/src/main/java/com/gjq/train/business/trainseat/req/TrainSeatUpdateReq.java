package com.gjq.train.business.trainseat.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainSeatUpdateReq extends TrainSeatSaveReq {

    @NotNull(message = "座位ID不能为空")
    private Long id;
}
