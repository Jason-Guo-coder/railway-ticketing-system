package com.gjq.train.business.dailytrainseat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.dailytrainseat.entity.DailyTrainSeat;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatQueryReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatSaveReq;
import com.gjq.train.business.dailytrainseat.req.DailyTrainSeatUpdateReq;
import com.gjq.train.business.dailytrainseat.resp.DailyTrainSeatQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.time.LocalDate;

/**
 * 每日座位业务接口。
 */
public interface DailyTrainSeatService extends IService<DailyTrainSeat> {

    void save(DailyTrainSeatSaveReq request);

    void delete(Long id);

    void update(DailyTrainSeatUpdateReq request);

    PageResp<DailyTrainSeatQueryResp> queryList(
            DailyTrainSeatQueryReq request
    );

    void generateByTrainCode(LocalDate date, String trainCode);

    int countSeat(LocalDate date, String trainCode, String seatType);
}
