package com.gjq.train.member.controller;

import com.gjq.train.common.resp.Result;
import com.gjq.train.member.req.MemberLoginReq;
import com.gjq.train.member.req.MemberRegisterReq;
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
    public Result<Long> count() {
        return Result.success(memberService.count());
    }

    @PostMapping("/register")
    public Result<Void> register(
            @Valid @RequestBody MemberRegisterReq memberRegisterReq) {
        memberService.register(memberRegisterReq);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<MemberLoginResp> login(
            @Valid @RequestBody MemberLoginReq memberLoginReq) {
        return Result.success(memberService.login(memberLoginReq));
    }
}
