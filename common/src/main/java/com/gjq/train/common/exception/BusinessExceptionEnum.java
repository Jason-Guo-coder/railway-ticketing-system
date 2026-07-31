package com.gjq.train.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目业务异常枚举。
 */
@Getter
@AllArgsConstructor
public enum BusinessExceptionEnum {

    MEMBER_MOBILE_EXIST(
            "MEMBER_MOBILE_EXIST",
            "手机号已经注册"
    );

    private final String code;

    private final String description;
}
