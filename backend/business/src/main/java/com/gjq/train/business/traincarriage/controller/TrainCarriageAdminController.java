package com.gjq.train.business.traincarriage.controller;

import com.gjq.train.business.traincarriage.req.TrainCarriageQueryReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageSaveReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageUpdateReq;
import com.gjq.train.business.traincarriage.resp.TrainCarriageQueryResp;
import com.gjq.train.business.traincarriage.service.TrainCarriageService;
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
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {

    @Resource
    private TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody TrainCarriageSaveReq request
    ) {
        trainCarriageService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody TrainCarriageUpdateReq request
    ) {
        trainCarriageService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<TrainCarriageQueryResp>> queryList(
            @Valid TrainCarriageQueryReq request
    ) {
        return Result.success(trainCarriageService.queryList(request));
    }
}
