package com.gjq.train.business.traincarriage.req;

import com.gjq.train.common.req.PageReq;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainCarriageQueryReq extends PageReq {

    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;
}
