package com.gjq.train.business.traincarriage.service;

import com.gjq.train.business.traincarriage.entity.TrainCarriage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.traincarriage.req.TrainCarriageQueryReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageSaveReq;
import com.gjq.train.business.traincarriage.req.TrainCarriageUpdateReq;
import com.gjq.train.business.traincarriage.resp.TrainCarriageQueryResp;
import com.gjq.train.common.resp.PageResp;

/**
 * <p>
 * 火车车厢 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
public interface TrainCarriageService extends IService<TrainCarriage> {

    void save(TrainCarriageSaveReq request);

    void delete(Long id);

    void update(TrainCarriageUpdateReq request);

    PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq request);
}
