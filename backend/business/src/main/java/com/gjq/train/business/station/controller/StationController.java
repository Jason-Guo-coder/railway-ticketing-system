package com.gjq.train.business.station.controller;

import com.gjq.train.business.station.resp.StationQueryResp;
import com.gjq.train.business.station.service.StationService;
import com.gjq.train.common.resp.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 普通用户车站查询接口。
 */
@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;

    @GetMapping("/query-all")
    public Result<List<StationQueryResp>> queryAll() {
        return Result.success(stationService.queryAll());
    }
}
