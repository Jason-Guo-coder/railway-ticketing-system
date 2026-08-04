package com.gjq.train.member.service;

import com.gjq.train.member.entity.Passenger;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjq.train.member.req.PassengerSaveReq;

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
}
