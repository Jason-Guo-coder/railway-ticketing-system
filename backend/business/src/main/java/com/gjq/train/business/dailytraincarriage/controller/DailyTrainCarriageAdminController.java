package com.gjq.train.business.dailytraincarriage.controller;

import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageQueryReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageSaveReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageUpdateReq;
import com.gjq.train.business.dailytraincarriage.resp.DailyTrainCarriageQueryResp;
import com.gjq.train.business.dailytraincarriage.service.DailyTrainCarriageService;
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
 * 管理员每日车厢接口。
 */
@RestController
@RequestMapping("/admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody DailyTrainCarriageSaveReq request
    ) {
        dailyTrainCarriageService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dailyTrainCarriageService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody DailyTrainCarriageUpdateReq request
    ) {
        dailyTrainCarriageService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<DailyTrainCarriageQueryResp>> queryList(
            @Valid DailyTrainCarriageQueryReq request
    ) {
        return Result.success(
                dailyTrainCarriageService.queryList(request)
        );
    }
}
