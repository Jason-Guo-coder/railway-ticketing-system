package com.gjq.train.business.station.controller;

import com.gjq.train.business.station.req.StationQueryReq;
import com.gjq.train.business.station.req.StationSaveReq;
import com.gjq.train.business.station.req.StationUpdateReq;
import com.gjq.train.business.station.resp.StationQueryResp;
import com.gjq.train.business.station.service.StationService;
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

import java.util.List;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService stationService;

    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody StationSaveReq request) {
        stationService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        stationService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody StationUpdateReq request) {
        stationService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<StationQueryResp>> queryList(
            @Valid StationQueryReq request
    ) {
        return Result.success(stationService.queryList(request));
    }

    @GetMapping("/query-all")
    public Result<List<StationQueryResp>> queryAll() {
        return Result.success(stationService.queryAll());
    }
}
