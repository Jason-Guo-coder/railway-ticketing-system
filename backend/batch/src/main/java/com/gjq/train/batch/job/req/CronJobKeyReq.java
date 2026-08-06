package com.gjq.train.batch.job.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Quartz任务定位请求，通过任务类名和分组组成唯一的JobKey。
 */
@Data
public class CronJobKeyReq {

    // 实现Quartz Job接口的任务类全限定名
    @NotBlank(message = "任务类名不能为空")
    private String name;

    // Quartz任务分组
    @NotBlank(message = "任务分组不能为空")
    private String group;
}
