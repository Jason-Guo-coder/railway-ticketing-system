package com.gjq.train.business.dailytrainstation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.dailytrainstation.entity.DailyTrainStation;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationQueryReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationSaveReq;
import com.gjq.train.business.dailytrainstation.req.DailyTrainStationUpdateReq;
import com.gjq.train.business.dailytrainstation.resp.DailyTrainStationQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.time.LocalDate;

/**
 * 每日车站业务接口。
 */
public interface DailyTrainStationService
        extends IService<DailyTrainStation> {

    void save(DailyTrainStationSaveReq request);

    void delete(Long id);

    void update(DailyTrainStationUpdateReq request);

    PageResp<DailyTrainStationQueryResp> queryList(
            DailyTrainStationQueryReq request
    );

    void generateByTrainCode(LocalDate date, String trainCode);
}
