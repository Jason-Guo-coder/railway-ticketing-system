package com.gjq.train.business.station.service;

import com.gjq.train.business.station.entity.Station;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.business.station.req.StationQueryReq;
import com.gjq.train.business.station.req.StationSaveReq;
import com.gjq.train.business.station.req.StationUpdateReq;
import com.gjq.train.business.station.resp.StationQueryResp;
import com.gjq.train.common.resp.PageResp;

import java.util.List;

/**
 * <p>
 * 车站 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-05
 */
public interface StationService extends IService<Station> {

    void save(StationSaveReq request);

    void delete(Long id);

    void update(StationUpdateReq request);

    PageResp<StationQueryResp> queryList(StationQueryReq request);

    List<StationQueryResp> queryAll();

    boolean existsByName(String name);
}
