package com.gjq.train.batch.job.config;

import com.gjq.train.batch.job.dailytrain.DailyTrainOneDayJob;
import com.gjq.train.batch.job.req.CronJobSaveReq;
import com.gjq.train.batch.job.resp.CronJobQueryResp;
import com.gjq.train.batch.job.service.CronJobService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultCronJobInitializerTests {

    @Test
    void shouldAddOnlyMissingDefaultJobs() {
        CronJobService cronJobService = mock(CronJobService.class);
        CronJobQueryResp existing = new CronJobQueryResp();
        existing.setName(DailyTrainOneDayJob.class.getName());
        existing.setGroup("DAILY_TRAIN");
        when(cronJobService.queryList()).thenReturn(List.of(existing));
        DefaultCronJobInitializer initializer =
                new DefaultCronJobInitializer(cronJobService);

        initializer.run(null);

        ArgumentCaptor<CronJobSaveReq> captor =
                ArgumentCaptor.forClass(CronJobSaveReq.class);
        verify(cronJobService, org.mockito.Mockito.times(2))
                .add(captor.capture());
        List<CronJobSaveReq> added = captor.getAllValues();
        assertEquals("0 10 2 * * ?", added.get(0).getCronExpression());
        assertEquals("0 20 2 * * ?", added.get(1).getCronExpression());
        assertEquals("DAILY_TRAIN", added.get(0).getGroup());
    }
}
