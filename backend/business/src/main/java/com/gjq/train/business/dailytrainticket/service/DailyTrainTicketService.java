package com.gjq.train.business.dailytrainticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketQueryReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketSaveReq;
import com.gjq.train.business.dailytrainticket.req.DailyTrainTicketUpdateReq;
import com.gjq.train.business.dailytrainticket.resp.DailyTrainTicketQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.time.LocalDate;

/**
 * 每日余票业务接口。
 */
public interface DailyTrainTicketService
        extends IService<DailyTrainTicket> {

    void save(DailyTrainTicketSaveReq request);

    void delete(Long id);

    void update(DailyTrainTicketUpdateReq request);

    PageResp<DailyTrainTicketQueryResp> queryList(
            DailyTrainTicketQueryReq request
    );

    void generateByTrainCode(
            LocalDate date,
            String trainCode,
            String trainType
    );
}
