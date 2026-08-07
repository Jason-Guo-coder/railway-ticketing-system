package com.gjq.train.batch.job.dailytrain;

import org.quartz.DisallowConcurrentExecution;

/**
 * 生成7天后的每日车次基础数据，保持近期车次数据完整。
 */
@DisallowConcurrentExecution
public class DailyTrainSevenDayJob extends AbstractDailyTrainJob {

    @Override
    protected int offsetDays() {
        return 7;
    }

    @Override
    protected String description() {
        return "生成7天后的每日车次数据";
    }
}
