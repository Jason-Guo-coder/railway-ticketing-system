package com.gjq.train.common.resp;

import lombok.Data;

@Data
public class MemberLoginResp {

    private Long id;

    private String mobile;

    private String token;
}
