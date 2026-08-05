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
    ),

    ADMIN_LOGIN_ERROR(
            "ADMIN_LOGIN_ERROR",
            "管理员账号或密码错误"
    ),

    BUSINESS_STATION_NAME_EXIST(
            "BUSINESS_STATION_NAME_EXIST",
            "车站名称已存在"
    ),

    BUSINESS_STATION_NOT_EXIST(
            "BUSINESS_STATION_NOT_EXIST",
            "车站不存在"
    ),

    BUSINESS_TRAIN_CODE_EXIST(
            "BUSINESS_TRAIN_CODE_EXIST",
            "车次编号已存在"
    ),

    BUSINESS_TRAIN_TYPE_INVALID(
            "BUSINESS_TRAIN_TYPE_INVALID",
            "车次类型无效"
    ),

    BUSINESS_TRAIN_NOT_EXIST(
            "BUSINESS_TRAIN_NOT_EXIST",
            "车次不存在"
    );

    private final String code;

    private final String description;
}
