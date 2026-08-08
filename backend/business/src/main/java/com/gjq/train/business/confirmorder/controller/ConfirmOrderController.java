package com.gjq.train.business.confirmorder.controller;

import com.gjq.train.business.confirmorder.req.ConfirmOrderDoReq;
import com.gjq.train.business.confirmorder.service.ConfirmOrderService;
import com.gjq.train.common.resp.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员端确认订单接口。
 */
@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/do")
    public Result<Void> doConfirm(
            @Valid @RequestBody ConfirmOrderDoReq request
    ) {
        confirmOrderService.doConfirm(request);
        return Result.success();
    }
}
