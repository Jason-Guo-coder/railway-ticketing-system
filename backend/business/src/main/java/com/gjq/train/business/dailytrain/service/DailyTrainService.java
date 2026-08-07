package com.gjq.train.business.dailytrain.service;

import com.gjq.train.business.dailytrain.entity.DailyTrain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.dailytrain.req.DailyTrainQueryReq;
import com.gjq.train.business.dailytrain.req.DailyTrainSaveReq;
import com.gjq.train.business.dailytrain.req.DailyTrainUpdateReq;
import com.gjq.train.business.dailytrain.resp.DailyTrainQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.time.LocalDate;

/**
 * <p>
 * 每日车次 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-06
 */
public interface DailyTrainService extends IService<DailyTrain> {

    void save(DailyTrainSaveReq request);

    void delete(Long id);

    void update(DailyTrainUpdateReq request);

    PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq request);

    void generate(LocalDate date);
}
