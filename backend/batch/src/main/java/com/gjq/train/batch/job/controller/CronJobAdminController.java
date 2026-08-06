package com.gjq.train.batch.job.controller;

import com.gjq.train.batch.job.req.CronJobKeyReq;
import com.gjq.train.batch.job.req.CronJobSaveReq;
import com.gjq.train.batch.job.resp.CronJobQueryResp;
import com.gjq.train.batch.job.service.CronJobService;
import com.gjq.train.common.resp.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Quartz任务管理接口，供Admin控台维护任务和手工触发任务。
 */
@RestController
@RequestMapping("/admin/job")
public class CronJobAdminController {

    // Quartz任务管理服务
    private final CronJobService cronJobService;

    public CronJobAdminController(CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody CronJobSaveReq request) {
        // 新增Quartz任务和Cron触发器
        cronJobService.add(request);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@Valid @RequestBody CronJobKeyReq request) {
        // 删除指定任务及其关联触发器
        cronJobService.delete(request);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody CronJobSaveReq request) {
        // 修改任务描述和Cron表达式
        cronJobService.update(request);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<List<CronJobQueryResp>> queryList() {
        // 查询Quartz中保存的全部Cron任务
        return Result.success(cronJobService.queryList());
    }

    @PostMapping("/pause")
    public Result<Void> pause(@Valid @RequestBody CronJobKeyReq request) {
        // 暂停指定任务
        cronJobService.pause(request);
        return Result.success();
    }

    @PostMapping("/resume")
    public Result<Void> resume(@Valid @RequestBody CronJobKeyReq request) {
        // 恢复指定任务
        cronJobService.resume(request);
        return Result.success();
    }

    @PostMapping("/run")
    public Result<Void> run(@Valid @RequestBody CronJobKeyReq request) {
        // 立即触发一次任务，用于手工补偿
        cronJobService.run(request);
        return Result.success();
    }
}
