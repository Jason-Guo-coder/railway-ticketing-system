package com.gjq.train.business.trainstation.req;

import com.gjq.train.common.req.PageReq;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainStationQueryReq extends PageReq {

    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;
}
