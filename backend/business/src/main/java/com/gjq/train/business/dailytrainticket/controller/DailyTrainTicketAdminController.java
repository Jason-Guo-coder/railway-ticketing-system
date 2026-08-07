package com.gjq.train.business.dailytrainticket.controller;

import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketQueryReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketSaveReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketUpdateReq;
import com.gjq.train.business.dailytrainticket.resp.DailyTrainTicketQueryResp;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.common.resp.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员每日余票接口。
 */
@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody DailyTrainTicketSaveReq request
    ) {
        dailyTrainTicketService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dailyTrainTicketService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody DailyTrainTicketUpdateReq request
    ) {
        dailyTrainTicketService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<DailyTrainTicketQueryResp>> queryList(
            @Valid DailyTrainTicketQueryReq request
    ) {
        return Result.success(
                dailyTrainTicketService.queryList(request)
        );
    }
}
