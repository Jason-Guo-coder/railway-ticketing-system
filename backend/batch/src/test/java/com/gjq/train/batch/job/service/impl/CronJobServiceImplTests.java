package com.gjq.train.batch.job.service.impl;

import com.gjq.train.batch.job.req.CronJobKeyReq;
import com.gjq.train.batch.job.req.CronJobSaveReq;
import com.gjq.train.batch.job.resp.CronJobQueryResp;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.CronTrigger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Quartz任务服务测试，使用Mock Scheduler验证调度操作。
 */
@ExtendWith(MockitoExtension.class)
class CronJobServiceImplTests {

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private CronJobServiceImpl cronJobService;

    @Test
    void shouldAddCronJob() throws Exception {
        CronJobSaveReq request = saveRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        cronJobService.add(request);

        ArgumentCaptor<JobDetail> jobCaptor =
                ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<CronTrigger> triggerCaptor =
                ArgumentCaptor.forClass(CronTrigger.class);
        verify(scheduler).scheduleJob(
                jobCaptor.capture(),
                triggerCaptor.capture()
        );
        assertEquals(request.getName(), jobCaptor.getValue()
                .getJobClass().getName());
        assertEquals(request.getCronExpression(), triggerCaptor.getValue()
                .getCronExpression());
    }

    @Test
    void shouldRejectDuplicateJob() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cronJobService.add(saveRequest())
        );

        assertEquals(
                BusinessExceptionEnum.BATCH_JOB_EXIST,
                exception.getExceptionEnum()
        );
        verify(scheduler, never()).scheduleJob(
                any(JobDetail.class),
                any(CronTrigger.class)
        );
    }

    @Test
    void shouldRejectJobOutsideBatchPackage() {
        CronJobSaveReq request = saveRequest();
        request.setName("java.lang.String");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cronJobService.add(request)
        );

        assertEquals(
                BusinessExceptionEnum.BATCH_JOB_CLASS_INVALID,
                exception.getExceptionEnum()
        );
    }

    @Test
    void shouldRejectInvalidCronExpression() {
        CronJobSaveReq request = saveRequest();
        request.setCronExpression("not-a-cron");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cronJobService.add(request)
        );

        assertEquals(
                BusinessExceptionEnum.BATCH_JOB_CRON_INVALID,
                exception.getExceptionEnum()
        );
    }

    @Test
    void shouldPauseResumeRunAndDeleteExistingJob() throws Exception {
        CronJobKeyReq request = keyRequest();
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        cronJobService.pause(request);
        cronJobService.resume(request);
        cronJobService.run(request);
        cronJobService.delete(request);

        JobKey jobKey = JobKey.jobKey(request.getName(), request.getGroup());
        verify(scheduler).pauseJob(jobKey);
        verify(scheduler).resumeJob(jobKey);
        verify(scheduler).triggerJob(jobKey);
        verify(scheduler).deleteJob(jobKey);
    }

    @Test
    void shouldRejectMissingJob() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cronJobService.pause(keyRequest())
        );

        assertEquals(
                BusinessExceptionEnum.BATCH_JOB_NOT_EXIST,
                exception.getExceptionEnum()
        );
    }

    @Test
    void shouldUpdateCronTriggerAndDescription() throws Exception {
        CronJobSaveReq request = saveRequest();
        request.setDescription("更新后的任务描述");
        request.setCronExpression("0/10 * * * * ?");
        JobKey jobKey = JobKey.jobKey(request.getName(), request.getGroup());
        TriggerKey triggerKey = TriggerKey.triggerKey(
                request.getName(),
                request.getGroup()
        );
        CronTrigger oldTrigger = cronTrigger(
                request.getName(),
                request.getGroup(),
                "0/5 * * * * ?"
        );
        JobDetail oldJobDetail = JobBuilder.newJob(
                        com.gjq.train.batch.job.CronJobTestJob.class
                )
                .withIdentity(jobKey)
                .withDescription("原任务描述")
                .build();
        when(scheduler.checkExists(jobKey)).thenReturn(true);
        when(scheduler.getTrigger(triggerKey)).thenReturn(oldTrigger);
        when(scheduler.getJobDetail(jobKey)).thenReturn(oldJobDetail);

        cronJobService.update(request);

        ArgumentCaptor<CronTrigger> triggerCaptor =
                ArgumentCaptor.forClass(CronTrigger.class);
        verify(scheduler).rescheduleJob(
                org.mockito.ArgumentMatchers.eq(triggerKey),
                triggerCaptor.capture()
        );
        assertEquals(request.getCronExpression(), triggerCaptor.getValue()
                .getCronExpression());

        ArgumentCaptor<JobDetail> jobCaptor =
                ArgumentCaptor.forClass(JobDetail.class);
        verify(scheduler).addJob(jobCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(true),
                org.mockito.ArgumentMatchers.eq(true));
        assertEquals(request.getDescription(), jobCaptor.getValue()
                .getDescription());
    }

    @Test
    void shouldQueryAndSortCronJobs() throws Exception {
        JobKey laterJob = JobKey.jobKey(
                "com.gjq.train.batch.job.CronJobTestJob",
                "DEMO"
        );
        JobKey firstJob = JobKey.jobKey(
                "com.gjq.train.batch.job.CronJobTestJob",
                "DEFAULT"
        );
        CronTrigger laterTrigger = cronTrigger(
                laterJob.getName(),
                laterJob.getGroup(),
                "0/10 * * * * ?"
        );
        CronTrigger firstTrigger = cronTrigger(
                firstJob.getName(),
                firstJob.getGroup(),
                "0/5 * * * * ?"
        );
        when(scheduler.getJobGroupNames())
                .thenReturn(List.of("DEMO", "DEFAULT"));
        when(scheduler.getJobKeys(any(GroupMatcher.class)))
                .thenAnswer(invocation -> {
                    GroupMatcher<JobKey> matcher = invocation.getArgument(0);
                    return "DEMO".equals(matcher.getCompareToValue())
                            ? Set.of(laterJob)
                            : Set.of(firstJob);
                });
        doReturn(List.<Trigger>of(laterTrigger))
                .when(scheduler).getTriggersOfJob(laterJob);
        doReturn(List.<Trigger>of(firstTrigger))
                .when(scheduler).getTriggersOfJob(firstJob);
        when(scheduler.getTriggerState(laterTrigger.getKey()))
                .thenReturn(Trigger.TriggerState.PAUSED);
        when(scheduler.getTriggerState(firstTrigger.getKey()))
                .thenReturn(Trigger.TriggerState.NORMAL);

        List<CronJobQueryResp> result = cronJobService.queryList();

        assertEquals(2, result.size());
        assertEquals("DEFAULT", result.get(0).getGroup());
        assertEquals(firstJob.getName(), result.get(0).getName());
        assertEquals("NORMAL", result.get(0).getState());
        assertEquals("PAUSED", result.get(1).getState());
    }

    private CronTrigger cronTrigger(
            String name,
            String group,
            String expression
    ) {
        return TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .withDescription("测试任务")
                .startAt(new Date(System.currentTimeMillis() + 60_000))
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .build();
    }

    private CronJobSaveReq saveRequest() {
        CronJobSaveReq request = new CronJobSaveReq();
        request.setName("com.gjq.train.batch.job.CronJobTestJob");
        request.setGroup("DEFAULT");
        request.setDescription("Quartz任务测试");
        request.setCronExpression("0/5 * * * * ?");
        return request;
    }

    private CronJobKeyReq keyRequest() {
        CronJobKeyReq request = new CronJobKeyReq();
        request.setName("com.gjq.train.batch.job.CronJobTestJob");
        request.setGroup("DEFAULT");
        return request;
    }
}
