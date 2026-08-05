package com.gjq.train.business.controller.admin;

import com.gjq.train.business.req.TrainQueryReq;
import com.gjq.train.business.req.TrainSaveReq;
import com.gjq.train.business.req.TrainUpdateReq;
import com.gjq.train.business.resp.TrainQueryResp;
import com.gjq.train.business.service.TrainService;
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
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody TrainSaveReq request) {
        trainService.save(request);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        trainService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody TrainUpdateReq request) {
        trainService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<TrainQueryResp>> queryList(
            @Valid TrainQueryReq request
    ) {
        return Result.success(trainService.queryList(request));
    }
}
