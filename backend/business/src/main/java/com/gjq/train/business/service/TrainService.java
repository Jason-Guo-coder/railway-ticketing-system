package com.gjq.train.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.entity.Train;
import com.gjq.train.business.req.TrainQueryReq;
import com.gjq.train.business.req.TrainSaveReq;
import com.gjq.train.business.req.TrainUpdateReq;
import com.gjq.train.business.resp.TrainQueryResp;
import com.gjq.train.common.resp.PageResp;

public interface TrainService extends IService<Train> {

    void save(TrainSaveReq request);

    void delete(Long id);

    void update(TrainUpdateReq request);

    PageResp<TrainQueryResp> queryList(TrainQueryReq request);
}
