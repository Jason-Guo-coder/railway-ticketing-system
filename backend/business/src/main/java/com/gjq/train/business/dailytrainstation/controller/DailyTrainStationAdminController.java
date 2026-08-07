package com.gjq.train.business.dailytrainstation.controller;

import com.gjq.train.business.dailytrainstation.req.DailyTrainStationQueryReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationSaveReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationUpdateReq;
import com.gjq.train.business.dailytrainstation.resp.DailyTrainStationQueryResp;
import com.gjq.train.business.dailytrainstation.service.DailyTrainStationService;
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
 * 管理员每日车站接口。
 */
@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody DailyTrainStationSaveReq request
    ) {
        dailyTrainStationService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dailyTrainStationService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody DailyTrainStationUpdateReq request
    ) {
        dailyTrainStationService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<DailyTrainStationQueryResp>> queryList(
            @Valid DailyTrainStationQueryReq request
    ) {
        return Result.success(
                dailyTrainStationService.queryList(request)
        );
    }
}
