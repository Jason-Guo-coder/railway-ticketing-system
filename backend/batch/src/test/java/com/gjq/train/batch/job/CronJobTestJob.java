package com.gjq.train.batch.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Quartz服务单元测试使用的最小任务类，不属于实际Batch业务。
 */
public class CronJobTestJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        // 测试任务不执行实际业务
    }
}
