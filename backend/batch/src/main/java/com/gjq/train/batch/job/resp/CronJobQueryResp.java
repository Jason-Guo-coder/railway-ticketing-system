package com.gjq.train.batch.job.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Quartz任务查询响应，用于Admin控台展示调度状态和执行时间。
 */
@Data
public class CronJobQueryResp {

    // 任务分组
    private String group;

    // 任务类全限定名
    private String name;

    // 任务用途说明
    private String description;

    // Quartz触发器状态，例如NORMAL、PAUSED、BLOCKED
    private String state;

    // Cron执行表达式
    private String cronExpression;

    // 下一次计划执行时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextFireTime;

    // 上一次实际执行时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date previousFireTime;
}
