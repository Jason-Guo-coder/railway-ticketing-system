package com.gjq.train.member.controller;

import com.gjq.train.common.resq.CommonResq;
import com.gjq.train.member.req.MemberRegisterReq;
import com.gjq.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/register")
    public CommonResq<Long> register(
            @Valid @ModelAttribute MemberRegisterReq memberRegisterReq) {
        return CommonResq.success(memberService.register(memberRegisterReq));
    }
}
