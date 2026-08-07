package com.gjq.train.batch.job.dailytrain;

import org.quartz.DisallowConcurrentExecution;

/**
 * 生成15天后的每日车次基础数据，对应预售期边界的数据生成任务。
 */
@DisallowConcurrentExecution
public class DailyTrainFifteenDayJob extends AbstractDailyTrainJob {

    @Override
    protected int offsetDays() {
        return 15;
    }

    @Override
    protected String description() {
        return "生成15天后的每日车次数据";
    }
}
