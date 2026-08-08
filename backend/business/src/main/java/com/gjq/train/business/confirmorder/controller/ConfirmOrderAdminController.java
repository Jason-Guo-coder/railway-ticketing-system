package com.gjq.train.business.confirmorder.controller;

import com.gjq.train.business.confirmorder.req.ConfirmOrderQueryReq;
import com.gjq.train.business.confirmorder.resp.ConfirmOrderQueryResp;
import com.gjq.train.business.confirmorder.service.ConfirmOrderService;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.common.resp.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员确认订单查询接口。
 */
@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    @GetMapping("/query-list")
    public Result<PageResp<ConfirmOrderQueryResp>> queryList(
            @Valid ConfirmOrderQueryReq request
    ) {
        return Result.success(confirmOrderService.queryList(request));
    }
}
