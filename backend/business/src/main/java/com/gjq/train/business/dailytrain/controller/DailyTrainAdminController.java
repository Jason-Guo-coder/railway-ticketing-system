package com.gjq.train.business.dailytrain.controller;

import com.gjq.train.business.dailytrain.req.DailyTrainQueryReq;
import com.gjq.train.business.dailytrain.req.DailyTrainSaveReq;
import com.gjq.train.business.dailytrain.req.DailyTrainUpdateReq;
import com.gjq.train.business.dailytrain.resp.DailyTrainQueryResp;
import com.gjq.train.business.dailytrain.service.DailyTrainService;
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

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody DailyTrainSaveReq request
    ) {
        dailyTrainService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dailyTrainService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody DailyTrainUpdateReq request
    ) {
        dailyTrainService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<DailyTrainQueryResp>> queryList(
            @Valid DailyTrainQueryReq request
    ) {
        return Result.success(dailyTrainService.queryList(request));
    }
}
