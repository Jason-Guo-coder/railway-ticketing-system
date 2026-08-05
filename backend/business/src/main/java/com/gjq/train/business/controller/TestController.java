package com.gjq.train.business.controller;

import com.gjq.train.common.resp.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("hello world business");
    }
}
