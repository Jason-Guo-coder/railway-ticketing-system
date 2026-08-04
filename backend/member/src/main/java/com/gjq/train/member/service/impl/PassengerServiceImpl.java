package com.gjq.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.member.entity.Passenger;
import com.gjq.train.member.mapper.PassengerMapper;
import com.gjq.train.member.req.PassengerSaveReq;
import com.gjq.train.member.service.PassengerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 乘车人 服务实现类
 * </p>
 *
 * @author 郭建泉
 * @since 2026-08-02
 */
@Service
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger> implements PassengerService {

    @Autowired
    PassengerMapper passengerMapper;

    @Override
    public void save(PassengerSaveReq passengerSaveReq) {
        //1. 将请求参数复制为乘车人实体
        Passenger passenger = BeanUtil.copyProperties(
                passengerSaveReq,
                Passenger.class
        );

        //2. 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        //3. 根据ID判断新增或更新
        if (passenger.getId() == null) {
            //4. 使用当前登录会员ID
            passenger.setMemberId(LoginMemberContext.getId());

            //5. 设置时间并新增乘车人
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            //6. 设置修改时间并更新乘车人
            passenger.setUpdateTime(now);
            passengerMapper.updateById(passenger);
        }
    }
}
