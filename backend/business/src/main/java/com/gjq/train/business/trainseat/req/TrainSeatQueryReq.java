package com.gjq.train.business.trainseat.req;

import com.gjq.train.common.req.PageReq;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainSeatQueryReq extends PageReq {

    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;
}
