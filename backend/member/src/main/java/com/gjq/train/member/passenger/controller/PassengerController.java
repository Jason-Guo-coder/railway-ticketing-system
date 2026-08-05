package com.gjq.train.member.passenger.controller;

import com.gjq.train.common.resp.Result;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.resp.PageResp;
import com.gjq.train.member.passenger.req.PassengerSaveReq;
import com.gjq.train.member.passenger.req.PassengerQueryReq;
import com.gjq.train.member.passenger.resp.PassengerQueryResp;
import com.gjq.train.member.passenger.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public Result<Void> save(
            @Valid @RequestBody PassengerSaveReq passengerSaveReq) {
        passengerService.save(passengerSaveReq);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(
            @Valid @RequestBody PassengerSaveReq passengerSaveReq) {
        passengerService.update(passengerSaveReq);
        return Result.success();
    }

    @GetMapping("/query-list")
    public Result<PageResp<PassengerQueryResp>> queryList(
            @Valid PassengerQueryReq passengerQueryReq) {
        //1. 使用登录上下文确定查询范围
        passengerQueryReq.setMemberId(LoginMemberContext.getId());

        //2. 查询当前会员的乘车人列表
        PageResp<PassengerQueryResp> pageResp = passengerService.queryList(
                passengerQueryReq
        );
        return Result.success(pageResp);
    }

}
