package com.gjq.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.member.entity.Passenger;
import com.gjq.train.member.mapper.PassengerMapper;
import com.gjq.train.member.req.PassengerQueryReq;
import com.gjq.train.member.req.PassengerSaveReq;
import com.gjq.train.member.resp.PassengerQueryResp;
import com.gjq.train.member.service.PassengerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

        //3. 使用当前登录会员ID
        passenger.setMemberId(LoginMemberContext.getId());

        //4. 设置时间并新增乘车人
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }

    @Override
    public void delete(Long id) {
        //1. 按乘车人ID和当前会员ID删除
        passengerMapper.delete(
                new LambdaQueryWrapper<Passenger>()
                        .eq(Passenger::getId, id)
                        .eq(Passenger::getMemberId, LoginMemberContext.getId())
        );
    }

    @Override
    public void update(PassengerSaveReq passengerSaveReq) {
        //1. 将请求参数复制为乘车人实体
        Passenger passenger = BeanUtil.copyProperties(
                passengerSaveReq,
                Passenger.class
        );

        //2. 使用当前会员ID限制编辑范围
        Long memberId = LoginMemberContext.getId();
        passenger.setMemberId(memberId);

        //3. 设置修改时间并更新当前会员的乘车人
        passenger.setUpdateTime(LocalDateTime.now());
        passengerMapper.update(
                passenger,
                new LambdaUpdateWrapper<Passenger>()
                        .eq(Passenger::getId, passenger.getId())
                        .eq(Passenger::getMemberId, memberId)
        );
    }

    @Override
    public PageResp<PassengerQueryResp> queryList(
            PassengerQueryReq passengerQueryReq) {
        //1. 构造当前会员的查询条件
        LambdaQueryWrapper<Passenger> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(
                        Passenger::getMemberId,
                        passengerQueryReq.getMemberId()
                )
                .orderByDesc(Passenger::getId);

        //2. 按请求页码和每页条数查询乘车人
        Page<Passenger> page = new Page<>(
                passengerQueryReq.getPage(),
                passengerQueryReq.getSize()
        );
        Page<Passenger> passengerPage = passengerMapper.selectPage(
                page,
                queryWrapper
        );

        //3. 将实体列表转换为接口响应对象
        List<PassengerQueryResp> list = BeanUtil.copyToList(
                passengerPage.getRecords(),
                PassengerQueryResp.class
        );

        //4. 组装分页响应结果
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(passengerPage.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
}
