package com.gjq.train.business.trainseat.controller;

import com.gjq.train.business.trainseat.req.TrainSeatQueryReq;
import com.gjq.train.business.trainseat.req.TrainSeatSaveReq;
import com.gjq.train.business.trainseat.req.TrainSeatUpdateReq;
import com.gjq.train.business.trainseat.resp.TrainSeatQueryResp;
import com.gjq.train.business.trainseat.service.TrainSeatService;
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
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {

    @Resource
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody TrainSeatSaveReq request) {
        trainSeatService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        trainSeatService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody TrainSeatUpdateReq request
    ) {
        trainSeatService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<TrainSeatQueryResp>> queryList(
            @Valid TrainSeatQueryReq request
    ) {
        return Result.success(trainSeatService.queryList(request));
    }
}
