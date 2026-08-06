package com.gjq.train.batch.job.service;

import com.gjq.train.batch.job.req.CronJobKeyReq;
import com.gjq.train.batch.job.req.CronJobSaveReq;
import com.gjq.train.batch.job.resp.CronJobQueryResp;

import java.util.List;

/**
 * Quartz任务管理服务，封装Admin控台需要的调度操作。
 */
public interface CronJobService {

    // 新增任务
    void add(CronJobSaveReq request);

    // 删除任务
    void delete(CronJobKeyReq request);

    // 修改任务
    void update(CronJobSaveReq request);

    // 查询全部Cron任务
    List<CronJobQueryResp> queryList();

    // 暂停任务
    void pause(CronJobKeyReq request);

    // 恢复任务
    void resume(CronJobKeyReq request);

    // 立即执行一次任务
    void run(CronJobKeyReq request);
}
