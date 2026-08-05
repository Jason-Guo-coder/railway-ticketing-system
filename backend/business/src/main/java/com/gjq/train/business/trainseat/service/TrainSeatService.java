package com.gjq.train.business.trainseat.service;

import com.gjq.train.business.trainseat.entity.TrainSeat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.trainseat.req.TrainSeatQueryReq;
import com.gjq.train.business.trainseat.req.TrainSeatSaveReq;
import com.gjq.train.business.trainseat.req.TrainSeatUpdateReq;
import com.gjq.train.business.trainseat.resp.TrainSeatQueryResp;
import com.gjq.train.common.resp.PageResp;

/**
 * <p>
 * 座位 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
public interface TrainSeatService extends IService<TrainSeat> {

    void save(TrainSeatSaveReq request);

    void delete(Long id);

    void update(TrainSeatUpdateReq request);

    PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq request);

    void generateByTrainCode(String trainCode);
}
