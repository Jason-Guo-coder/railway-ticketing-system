package com.gjq.train.batch.job.dailytrain;

import com.gjq.train.batch.feign.BusinessFeign;
import com.gjq.train.common.resp.Result;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DailyTrainJobTests {

    @Test
    void shouldGenerateOneDayAhead() throws Exception {
        verifyTargetDate(new DailyTrainOneDayJob(), 1);
    }

    @Test
    void shouldGenerateSevenDaysAhead() throws Exception {
        verifyTargetDate(new DailyTrainSevenDayJob(), 7);
    }

    @Test
    void shouldGenerateFifteenDaysAhead() throws Exception {
        verifyTargetDate(new DailyTrainFifteenDayJob(), 15);
    }

    @Test
    void shouldFailWhenBusinessRejectsGeneration() {
        AbstractDailyTrainJob job = new DailyTrainOneDayJob();
        BusinessFeign client = mock(BusinessFeign.class);
        ReflectionTestUtils.setField(job, "businessFeign", client);
        when(client.generateDaily(any(LocalDate.class)))
                .thenReturn(Result.fail("生成失败"));

        JobExecutionException exception = assertThrows(
                JobExecutionException.class,
                () -> job.execute(mock(JobExecutionContext.class))
        );

        assertEquals("生成失败", exception.getCause().getMessage());
    }

    private void verifyTargetDate(
            AbstractDailyTrainJob job,
            int offsetDays
    ) throws Exception {
        BusinessFeign client = mock(BusinessFeign.class);
        ReflectionTestUtils.setField(
                job,
                "businessFeign",
                client
        );
        LocalDate before = LocalDate.now().plusDays(offsetDays);
        when(client.generateDaily(any(LocalDate.class)))
                .thenReturn(Result.success());

        job.execute(mock(JobExecutionContext.class));

        LocalDate after = LocalDate.now().plusDays(offsetDays);
        verify(client).generateDaily(argThat(
                date -> date.equals(before) || date.equals(after)
        ));
    }
}
