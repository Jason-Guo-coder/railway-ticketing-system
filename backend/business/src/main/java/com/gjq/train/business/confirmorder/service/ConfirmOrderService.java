package com.gjq.train.business.confirmorder.service;

import com.gjq.train.business.confirmorder.entity.ConfirmOrder;
import com.gjq.train.business.confirmorder.req.ConfirmOrderDoReq;
import com.gjq.train.business.confirmorder.req.ConfirmOrderQueryReq;
import com.gjq.train.business.confirmorder.resp.ConfirmOrderQueryResp;
import com.gjq.train.common.resp.PageResp;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 确认订单业务接口。
 */
public interface ConfirmOrderService extends IService<ConfirmOrder> {

    void doConfirm(ConfirmOrderDoReq request);

    PageResp<ConfirmOrderQueryResp> queryList(
            ConfirmOrderQueryReq request
    );
}
