package com.gjq.train.business.trainstation.controller;

import com.gjq.train.business.trainstation.req.TrainStationQueryReq;
import com.gjq.train.business.trainstation.req.TrainStationSaveReq;
import com.gjq.train.business.trainstation.req.TrainStationUpdateReq;
import com.gjq.train.business.trainstation.resp.TrainStationQueryResp;
import com.gjq.train.business.trainstation.service.TrainStationService;
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
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

    @Resource
    private TrainStationService trainStationService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody TrainStationSaveReq request
    ) {
        trainStationService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        trainStationService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody TrainStationUpdateReq request
    ) {
        trainStationService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<TrainStationQueryResp>> queryList(
            @Valid TrainStationQueryReq request
    ) {
        return Result.success(trainStationService.queryList(request));
    }
}
