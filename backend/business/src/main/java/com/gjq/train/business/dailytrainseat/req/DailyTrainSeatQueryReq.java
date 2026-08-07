package com.gjq.train.business.dailytrainseat.req;

import com.gjq.train.common.req.PageReq;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 每日座位分页查询请求。
 */
@Data
public class DailyTrainSeatQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Size(max = 20, message = "车次编号不能超过20个字符")
    private String trainCode;
}
