package com.gjq.train.member.controller;

import com.gjq.train.common.resq.CommonResq;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public CommonResq<String> hello() {
        return CommonResq.success("hello world");
    }

}
