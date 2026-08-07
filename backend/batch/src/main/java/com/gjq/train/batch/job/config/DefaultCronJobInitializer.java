package com.gjq.train.batch.job.config;

import com.gjq.train.batch.job.dailytrain.DailyTrainFifteenDayJob;
import com.gjq.train.batch.job.dailytrain.DailyTrainOneDayJob;
import com.gjq.train.batch.job.dailytrain.DailyTrainSevenDayJob;
import com.gjq.train.batch.job.req.CronJobSaveReq;
import com.gjq.train.batch.job.resp.CronJobQueryResp;
import com.gjq.train.batch.job.service.CronJobService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Batch启动时补充项目默认的三项每日车次生成任务。
 */
@Component
@ConditionalOnProperty(
        name = "batch.jobs.initialize",
        havingValue = "true",
        matchIfMissing = true
)
public class DefaultCronJobInitializer implements ApplicationRunner {

    private static final String GROUP = "DAILY_TRAIN";

    private static final List<JobDefinition> DEFAULT_JOBS = List.of(
            new JobDefinition(
                    DailyTrainOneDayJob.class.getName(),
                    "生成1天后的每日车次、车站、车厢和座位数据",
                    "0 0 2 * * ?"
            ),
            new JobDefinition(
                    DailyTrainSevenDayJob.class.getName(),
                    "生成7天后的每日车次、车站、车厢和座位数据",
                    "0 10 2 * * ?"
            ),
            new JobDefinition(
                    DailyTrainFifteenDayJob.class.getName(),
                    "生成15天后的每日车次、车站、车厢和座位数据",
                    "0 20 2 * * ?"
            )
    );

    private final CronJobService cronJobService;

    public DefaultCronJobInitializer(CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    @Override
    public void run(ApplicationArguments args) {
        //1. 查询Quartz中已经持久化的任务，保留用户后续修改的Cron配置
        Set<String> existingJobs = cronJobService.queryList().stream()
                .map(item -> key(item.getName(), item.getGroup()))
                .collect(Collectors.toSet());

        //2. 只创建尚不存在的默认任务
        for (JobDefinition definition : DEFAULT_JOBS) {
            //① 已存在的任务不重复注册，也不覆盖页面中编辑后的配置
            if (existingJobs.contains(key(definition.name(), GROUP))) {
                continue;
            }
            //② 使用现有任务服务写入Quartz JDBC JobStore
            cronJobService.add(toRequest(definition));
        }
    }

    private CronJobSaveReq toRequest(JobDefinition definition) {
        CronJobSaveReq request = new CronJobSaveReq();
        request.setName(definition.name());
        request.setGroup(GROUP);
        request.setDescription(definition.description());
        request.setCronExpression(definition.cronExpression());
        return request;
    }

    private String key(String name, String group) {
        return group + ":" + name;
    }

    private record JobDefinition(
            String name,
            String description,
            String cronExpression
    ) {
    }
}
