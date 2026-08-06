package com.gjq.train.batch.job.service.impl;

import com.gjq.train.batch.job.req.CronJobKeyReq;
import com.gjq.train.batch.job.req.CronJobSaveReq;
import com.gjq.train.batch.job.resp.CronJobQueryResp;
import com.gjq.train.batch.job.service.CronJobService;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Quartz任务管理服务实现，负责把Admin操作转换为Scheduler调度操作。
 */
@Service
public class CronJobServiceImpl implements CronJobService {

    // 只允许管理Batch模块下的任务类
    private static final String JOB_PACKAGE = "com.gjq.train.batch.job.";

    // Quartz调度器，由Spring Boot根据application.yml自动创建
    private final Scheduler scheduler;

    public CronJobServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void add(CronJobSaveReq request) {
        //1. 校验任务类、Cron表达式和任务唯一性
        Class<? extends Job> jobClass = requireJobClass(request.getName());
        requireValidCron(request.getCronExpression());
        JobKey jobKey = jobKey(request);

        try {
            if (scheduler.checkExists(jobKey)) {
                throw new BusinessException(
                        BusinessExceptionEnum.BATCH_JOB_EXIST
                );
            }

            //2. 构建任务和Cron触发器
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobKey)
                    .withDescription(request.getDescription())
                    .build();
            CronTrigger trigger = newTrigger(request);

            //3. 保存到Quartz调度器，JDBC JobStore会同步持久化
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }
    }

    @Override
    public void delete(CronJobKeyReq request) {
        //1. 校验任务是否存在
        JobKey jobKey = requireJob(request);
        try {
            //2. 删除任务和关联触发器
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }
    }

    @Override
    public void update(CronJobSaveReq request) {
        //1. 校验任务和Cron表达式
        JobKey jobKey = requireJob(request);
        requireValidCron(request.getCronExpression());
        TriggerKey triggerKey = triggerKey(request);

        try {
            Trigger oldTrigger = scheduler.getTrigger(triggerKey);
            if (!(oldTrigger instanceof CronTrigger cronTrigger)) {
                throw new BusinessException(
                        BusinessExceptionEnum.BATCH_JOB_NOT_EXIST
                );
            }

            //2. 使用原触发器重新构建，保留任务关联并更新表达式
            CronTrigger newTrigger = cronTrigger.getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .withDescription(request.getDescription())
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(
                            request.getCronExpression()
                    ))
                    .build();

            //3. 替换触发器并更新任务描述
            scheduler.rescheduleJob(triggerKey, newTrigger);
            JobDetail newJobDetail = scheduler.getJobDetail(jobKey)
                    .getJobBuilder()
                    .withDescription(request.getDescription())
                    .build();
            scheduler.addJob(newJobDetail, true, true);
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }
    }

    @Override
    public List<CronJobQueryResp> queryList() {
        List<CronJobQueryResp> result = new ArrayList<>();
        try {
            //1. 遍历所有任务分组
            for (String group : scheduler.getJobGroupNames()) {
                //② 遍历当前分组中的全部任务
                for (JobKey jobKey : scheduler.getJobKeys(
                        GroupMatcher.jobGroupEquals(group)
                )) {
                    //③ Quartz允许一个任务有多个触发器，逐个转换展示
                    for (Trigger trigger : scheduler.getTriggersOfJob(jobKey)) {
                        if (trigger instanceof CronTrigger cronTrigger) {
                            result.add(toResponse(jobKey, cronTrigger));
                        }
                    }
                }
            }
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }

        //2. 固定按分组和类名排序，避免页面刷新时顺序跳动
        result.sort(Comparator.comparing(CronJobQueryResp::getGroup)
                .thenComparing(CronJobQueryResp::getName));
        return result;
    }

    @Override
    public void pause(CronJobKeyReq request) {
        //1. 校验任务是否存在
        JobKey jobKey = requireJob(request);
        try {
            //2. 暂停任务的全部触发器
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }
    }

    @Override
    public void resume(CronJobKeyReq request) {
        //1. 校验任务是否存在
        JobKey jobKey = requireJob(request);
        try {
            //2. 恢复任务的全部触发器
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }
    }

    @Override
    public void run(CronJobKeyReq request) {
        //1. 校验任务是否存在
        JobKey jobKey = requireJob(request);
        try {
            //2. 立即触发一次任务，不改变原有Cron计划
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }
    }

    private CronJobQueryResp toResponse(
            JobKey jobKey,
            CronTrigger trigger
    ) throws SchedulerException {
        // 创建页面展示所需的任务信息
        CronJobQueryResp response = new CronJobQueryResp();
        response.setName(jobKey.getName());
        response.setGroup(jobKey.getGroup());
        response.setDescription(trigger.getDescription());
        response.setState(
                scheduler.getTriggerState(trigger.getKey()).name()
        );
        response.setCronExpression(trigger.getCronExpression());
        response.setPreviousFireTime(trigger.getPreviousFireTime());
        response.setNextFireTime(trigger.getNextFireTime());
        return response;
    }

    private CronTrigger newTrigger(CronJobSaveReq request) {
        // 创建任务对应的Cron触发器
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey(request))
                .withDescription(request.getDescription())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(
                        request.getCronExpression()
                ))
                .build();
    }

    private Class<? extends Job> requireJobClass(String className) {
        //1. 限制任务类只能来自Batch任务包
        if (!className.startsWith(JOB_PACKAGE)) {
            throw new BusinessException(
                    BusinessExceptionEnum.BATCH_JOB_CLASS_INVALID
            );
        }
        try {
            //2. 加载任务类并确认它实现了Quartz Job接口
            return Class.forName(className).asSubclass(Job.class);
        } catch (ClassNotFoundException | ClassCastException exception) {
            throw new BusinessException(
                    BusinessExceptionEnum.BATCH_JOB_CLASS_INVALID
            );
        }
    }

    private void requireValidCron(String cronExpression) {
        // 校验Quartz Cron表达式格式
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new BusinessException(
                    BusinessExceptionEnum.BATCH_JOB_CRON_INVALID
            );
        }
    }

    private JobKey requireJob(CronJobKeyReq request) {
        //1. 根据任务类名和分组创建唯一JobKey
        JobKey jobKey = jobKey(request);
        try {
            //2. 访问Quartz确认任务是否存在
            if (!scheduler.checkExists(jobKey)) {
                throw new BusinessException(
                        BusinessExceptionEnum.BATCH_JOB_NOT_EXIST
                );
            }
            return jobKey;
        } catch (SchedulerException exception) {
            throw schedulerException(exception);
        }
    }

    private JobKey jobKey(CronJobKeyReq request) {
        // 根据请求构造Quartz任务唯一标识
        return JobKey.jobKey(request.getName(), request.getGroup());
    }

    private TriggerKey triggerKey(CronJobKeyReq request) {
        // 根据请求构造Cron触发器唯一标识
        return TriggerKey.triggerKey(request.getName(), request.getGroup());
    }

    private BusinessException schedulerException(
            SchedulerException exception
    ) {
        // 把Quartz底层异常转换为项目统一业务异常
        return new BusinessException(
                BusinessExceptionEnum.BATCH_JOB_SCHEDULER_ERROR
        );
    }
}
