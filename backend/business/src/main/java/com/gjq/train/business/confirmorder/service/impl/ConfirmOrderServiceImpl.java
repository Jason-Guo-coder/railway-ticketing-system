package com.gjq.train.business.confirmorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjq.train.business.confirmorder.entity.ConfirmOrder;
import com.gjq.train.business.confirmorder.enums.ConfirmOrderStatusEnum;
import com.gjq.train.business.confirmorder.mapper.ConfirmOrderMapper;
import com.gjq.train.business.confirmorder.req.ConfirmOrderDoReq;
import com.gjq.train.business.confirmorder.req.ConfirmOrderQueryReq;
import com.gjq.train.business.confirmorder.resp.ConfirmOrderQueryResp;
import com.gjq.train.business.confirmorder.service.ConfirmOrderService;
import com.gjq.train.business.dailytrainticket.entity.DailyTrainTicket;
import com.gjq.train.business.dailytrainticket.service.DailyTrainTicketService;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 确认订单业务实现。
 */
@Service
public class ConfirmOrderServiceImpl
        extends ServiceImpl<ConfirmOrderMapper, ConfirmOrder>
        implements ConfirmOrderService {

    private static final Logger LOG =
            LoggerFactory.getLogger(ConfirmOrderServiceImpl.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Override
    public void doConfirm(ConfirmOrderDoReq request) {
        //1. 使用当前登录会员保存初始状态的确认订单
        LocalDateTime now = LocalDateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(request.getDate());
        confirmOrder.setTrainCode(request.getTrainCode());
        confirmOrder.setStart(request.getStart());
        confirmOrder.setEnd(request.getEnd());
        confirmOrder.setDailyTrainTicketId(
                request.getDailyTrainTicketId()
        );
        confirmOrder.setTickets(JSONUtil.toJsonStr(request.getTickets()));
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrderMapper.insert(confirmOrder);

        //2. 按业务唯一键查询数据库中的真实余票
        DailyTrainTicket dailyTrainTicket =
                dailyTrainTicketService.selectByUnique(
                        request.getDate(),
                        request.getTrainCode(),
                        request.getStart(),
                        request.getEnd()
                );
        LOG.info(
                "真实余票记录ID：{}",
                dailyTrainTicket == null ? null : dailyTrainTicket.getId()
        );
    }

    @Override
    public PageResp<ConfirmOrderQueryResp> queryList(
            ConfirmOrderQueryReq request
    ) {
        //1. 按日期、车次和创建时间构造倒序分页查询
        Page<ConfirmOrder> page = new Page<>(
                request.getPage(),
                request.getSize()
        );
        LambdaQueryWrapper<ConfirmOrder> queryWrapper =
                new LambdaQueryWrapper<ConfirmOrder>()
                        .orderByDesc(ConfirmOrder::getDate)
                        .orderByAsc(ConfirmOrder::getTrainCode)
                        .orderByDesc(ConfirmOrder::getCreateTime);

        //2. 查询确认订单分页数据
        Page<ConfirmOrder> orderPage =
                confirmOrderMapper.selectPage(page, queryWrapper);

        //3. 转换响应对象并组装分页结果
        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(
                orderPage.getRecords(),
                ConfirmOrderQueryResp.class
        );
        PageResp<ConfirmOrderQueryResp> response = new PageResp<>();
        response.setTotal(orderPage.getTotal());
        response.setList(list);
        return response;
    }

}
