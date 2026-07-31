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
    ),

    MEMBER_MOBILE_NOT_EXIST(
            "MEMBER_MOBILE_NOT_EXIST",
            "请先获取短信验证码"
    ),

    MEMBER_MOBILE_CODE_ERROR(
            "MEMBER_MOBILE_CODE_ERROR",
            "短信验证码错误"
    );

    private final String code;

    private final String description;
}
