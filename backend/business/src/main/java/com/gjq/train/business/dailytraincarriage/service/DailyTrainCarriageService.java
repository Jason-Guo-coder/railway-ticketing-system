package com.gjq.train.business.dailytraincarriage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.dailytraincarriage.entity.DailyTrainCarriage;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageQueryReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageSaveReq;
import com.gjq.train.business.dailytraincarriage.req.DailyTrainCarriageUpdateReq;
import com.gjq.train.business.dailytraincarriage.resp.DailyTrainCarriageQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.time.LocalDate;

/**
 * 每日车厢业务接口。
 */
public interface DailyTrainCarriageService
        extends IService<DailyTrainCarriage> {

    void save(DailyTrainCarriageSaveReq request);

    void delete(Long id);

    void update(DailyTrainCarriageUpdateReq request);

    PageResp<DailyTrainCarriageQueryResp> queryList(
            DailyTrainCarriageQueryReq request
    );

    void generateByTrainCode(LocalDate date, String trainCode);
}
