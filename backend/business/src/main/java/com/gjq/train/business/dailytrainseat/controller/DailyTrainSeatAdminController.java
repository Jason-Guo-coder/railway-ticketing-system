package com.gjq.train.business.dailytrainseat.controller;

import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatQueryReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatSaveReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatUpdateReq;
import com.gjq.train.business.dailytrainseat.resp.DailyTrainSeatQueryResp;
import com.gjq.train.business.dailytrainseat.service.DailyTrainSeatService;
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
 * 管理员每日座位接口。
 */
@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody DailyTrainSeatSaveReq request
    ) {
        dailyTrainSeatService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dailyTrainSeatService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody DailyTrainSeatUpdateReq request
    ) {
        dailyTrainSeatService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<DailyTrainSeatQueryResp>> queryList(
            @Valid DailyTrainSeatQueryReq request
    ) {
        return Result.success(dailyTrainSeatService.queryList(request));
    }
}
