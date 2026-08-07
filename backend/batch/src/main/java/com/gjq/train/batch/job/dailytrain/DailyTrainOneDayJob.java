package com.gjq.train.batch.job.dailytrain;

import org.quartz.DisallowConcurrentExecution;

/**
 * 生成1天后的每日车次基础数据，作为临近发车前的数据补偿任务。
 */
@DisallowConcurrentExecution
public class DailyTrainOneDayJob extends AbstractDailyTrainJob {

    @Override
    protected int offsetDays() {
        return 1;
    }

    @Override
    protected String description() {
        return "生成1天后的每日车次数据";
    }
}
