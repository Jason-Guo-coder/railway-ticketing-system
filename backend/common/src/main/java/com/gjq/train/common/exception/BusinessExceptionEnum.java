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
    ),

    BUSINESS_DAILY_TRAIN_DATE_CODE_EXIST(
            "BUSINESS_DAILY_TRAIN_DATE_CODE_EXIST",
            "该日期的车次编号已存在"
    ),

    BUSINESS_DAILY_TRAIN_NOT_EXIST(
            "BUSINESS_DAILY_TRAIN_NOT_EXIST",
            "每日车次不存在"
    ),

    BUSINESS_TRAIN_STATION_INDEX_EXIST(
            "BUSINESS_TRAIN_STATION_INDEX_EXIST",
            "该车次的站序已存在"
    ),

    BUSINESS_TRAIN_STATION_NAME_EXIST(
            "BUSINESS_TRAIN_STATION_NAME_EXIST",
            "该车次的站名已存在"
    ),

    BUSINESS_TRAIN_STATION_NOT_EXIST(
            "BUSINESS_TRAIN_STATION_NOT_EXIST",
            "车次车站不存在"
    ),

    BUSINESS_TRAIN_CARRIAGE_INDEX_EXIST(
            "BUSINESS_TRAIN_CARRIAGE_INDEX_EXIST",
            "该车次的厢号已存在"
    ),

    BUSINESS_TRAIN_CARRIAGE_SEAT_TYPE_INVALID(
            "BUSINESS_TRAIN_CARRIAGE_SEAT_TYPE_INVALID",
            "座位类型无效"
    ),

    BUSINESS_TRAIN_CARRIAGE_NOT_EXIST(
            "BUSINESS_TRAIN_CARRIAGE_NOT_EXIST",
            "火车车厢不存在"
    ),

    BUSINESS_TRAIN_CARRIAGE_EMPTY(
            "BUSINESS_TRAIN_CARRIAGE_EMPTY",
            "请先为该车次维护车厢信息"
    ),

    BUSINESS_TRAIN_SEAT_LOCATION_EXIST(
            "BUSINESS_TRAIN_SEAT_LOCATION_EXIST",
            "该车厢的座位位置已存在"
    ),

    BUSINESS_TRAIN_SEAT_INDEX_EXIST(
            "BUSINESS_TRAIN_SEAT_INDEX_EXIST",
            "该车厢的座序已存在"
    ),

    BUSINESS_TRAIN_SEAT_TYPE_INVALID(
            "BUSINESS_TRAIN_SEAT_TYPE_INVALID",
            "座位类型无效"
    ),

    BUSINESS_TRAIN_SEAT_COL_INVALID(
            "BUSINESS_TRAIN_SEAT_COL_INVALID",
            "列号与座位类型不匹配"
    ),

    BUSINESS_TRAIN_SEAT_NOT_EXIST(
            "BUSINESS_TRAIN_SEAT_NOT_EXIST",
            "座位不存在"
    ),

    // Quartz任务已存在
    BATCH_JOB_EXIST(
            "BATCH_JOB_EXIST",
            "定时任务已存在"
    ),

    // Quartz任务不存在
    BATCH_JOB_NOT_EXIST(
            "BATCH_JOB_NOT_EXIST",
            "定时任务不存在"
    ),

    // Quartz任务类不符合Batch模块约定
    BATCH_JOB_CLASS_INVALID(
            "BATCH_JOB_CLASS_INVALID",
            "任务类不存在或不是有效的Quartz任务"
    ),

    // Quartz Cron表达式格式错误
    BATCH_JOB_CRON_INVALID(
            "BATCH_JOB_CRON_INVALID",
            "Cron表达式格式错误"
    ),

    // Quartz调度器操作失败
    BATCH_JOB_SCHEDULER_ERROR(
            "BATCH_JOB_SCHEDULER_ERROR",
            "定时任务调度失败"
    );

    private final String code;

    private final String description;
}
