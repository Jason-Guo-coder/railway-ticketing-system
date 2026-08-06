package com.gjq.train.batch.job.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Quartz任务保存请求，包含任务标识、描述和执行周期。
 */
@Data
public class CronJobSaveReq extends CronJobKeyReq {

    // Admin控台展示的任务用途说明
    @Size(max = 250, message = "任务描述不能超过250个字符")
    private String description;

    // Quartz格式的Cron执行表达式
    @NotBlank(message = "Cron表达式不能为空")
    private String cronExpression;
}
