package com.gjq.train.batch.job.dailytrain;

import com.gjq.train.batch.feign.BusinessFeign;
import com.gjq.train.common.resp.Result;
import jakarta.annotation.Resource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDate;

/**
 * 每日车次生成任务基类，统一计算目标日期和处理执行日志。
 */
public abstract class AbstractDailyTrainJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDailyTrainJob.class);

    @Resource
    private BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        MDC.put("LOG_ID", Long.toString(System.currentTimeMillis()));
        LocalDate date = LocalDate.now().plusDays(offsetDays());

        try {
            //1. 根据任务偏移天数计算需要生成数据的日期
            LOG.info("{}开始，目标日期：{}", description(), date);
            //2. 调用Business生成车次、车站、车厢和座位数据
            Result<Void> result = businessFeign.generateDaily(date);
            //3. HTTP调用成功但业务处理失败时，将本次任务标记为失败
            if (result == null || !result.isSuccess()) {
                String message = result == null
                        ? "Business接口未返回结果"
                        : result.getMessage();
                throw new IllegalStateException(message);
            }
            LOG.info("{}完成，目标日期：{}", description(), date);
        } catch (RuntimeException exception) {
            LOG.error("{}失败，目标日期：{}", description(), date, exception);
            throw new JobExecutionException(
                    description() + "失败",
                    exception,
                    false
            );
        } finally {
            MDC.remove("LOG_ID");
        }
    }

    protected abstract int offsetDays();

    protected abstract String description();
}
