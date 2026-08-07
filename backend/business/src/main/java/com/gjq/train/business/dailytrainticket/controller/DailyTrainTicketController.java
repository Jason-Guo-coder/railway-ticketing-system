package com.gjq.train.business.dailytrainticket.controller;

import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketQueryReq;
import com.gjq.train.business.dailytrainticket.resp.DailyTrainTicketQueryResp;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.common.resp.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员端余票查询接口。
 */
@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public Result<PageResp<DailyTrainTicketQueryResp>> queryList(
            @Valid DailyTrainTicketQueryReq request
    ) {
        return Result.success(
                dailyTrainTicketService.queryList(request)
        );
    }
}
