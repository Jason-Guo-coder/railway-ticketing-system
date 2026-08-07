package com.gjq.train.business.train.controller;

import com.gjq.train.business.train.resp.TrainQueryResp;
import com.gjq.train.business.train.service.TrainService;
import com.gjq.train.common.resp.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 普通用户车次查询接口。
 */
@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;

    @GetMapping("/query-all")
    public Result<List<TrainQueryResp>> queryAll() {
        return Result.success(trainService.queryAll());
    }
}
