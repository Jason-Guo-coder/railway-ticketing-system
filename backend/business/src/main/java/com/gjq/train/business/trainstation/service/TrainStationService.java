package com.gjq.train.business.trainstation.service;

import com.gjq.train.business.trainstation.entity.TrainStation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.trainstation.req.TrainStationQueryReq;
import com.gjq.train.business.trainstation.req.TrainStationSaveReq;
import com.gjq.train.business.trainstation.req.TrainStationUpdateReq;
import com.gjq.train.business.trainstation.resp.TrainStationQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.util.List;

/**
 * <p>
 * 火车车站 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
public interface TrainStationService extends IService<TrainStation> {

    void save(TrainStationSaveReq request);

    void delete(Long id);

    void update(TrainStationUpdateReq request);

    PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq request);

    List<TrainStation> listByTrainCode(String trainCode);
}
