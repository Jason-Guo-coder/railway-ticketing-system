package com.gjq.train.member.passenger.service;

import com.gjq.train.member.passenger.entity.Passenger;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.member.passenger.req.PassengerQueryReq;
import com.gjq.train.member.passenger.req.PassengerSaveReq;
import com.gjq.train.member.passenger.resp.PassengerQueryResp;

/**
 * <p>
 * 乘车人 服务类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-02
 */
public interface PassengerService extends IService<Passenger> {
    void save(PassengerSaveReq passengerSaveReq);

    void delete(Long id);

    void update(PassengerSaveReq passengerSaveReq);

    PageResp<PassengerQueryResp> queryList(PassengerQueryReq passengerQueryReq);
}
