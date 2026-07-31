package com.gjq.train.member.controller;

import com.gjq.train.common.resq.CommonResq;
import com.gjq.train.member.req.MemberLoginReq;
import com.gjq.train.member.req.MemberSendCodeReq;
import com.gjq.train.member.resp.MemberLoginResp;
import com.gjq.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public CommonResq<Long> count() {
        return CommonResq.success(memberService.count());
    }

    @PostMapping("/send-code")
    public CommonResq<Void> sendCode(
            @Valid @RequestBody MemberSendCodeReq memberSendCodeReq) {
        memberService.sendCode(memberSendCodeReq);
        return CommonResq.success();
    }

    @PostMapping("/login")
    public CommonResq<MemberLoginResp> login(
            @Valid @RequestBody MemberLoginReq memberLoginReq) {
        return CommonResq.success(memberService.login(memberLoginReq));
    }
}
