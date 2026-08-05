package com.gjq.train.business.train.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.train.entity.Train;
import com.gjq.train.business.train.req.TrainQueryReq;
import com.gjq.train.business.train.req.TrainSaveReq;
import com.gjq.train.business.train.req.TrainUpdateReq;
import com.gjq.train.business.train.resp.TrainQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.util.List;

public interface TrainService extends IService<Train> {

    void save(TrainSaveReq request);

    void delete(Long id);

    void update(TrainUpdateReq request);

    PageResp<TrainQueryResp> queryList(TrainQueryReq request);

    List<TrainQueryResp> queryAll();
}
