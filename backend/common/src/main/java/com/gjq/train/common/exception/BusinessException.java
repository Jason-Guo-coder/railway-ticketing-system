package com.gjq.train.common.exception;

import lombok.Getter;

/**
 * 项目业务异常。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final BusinessExceptionEnum exceptionEnum;

    public BusinessException(BusinessExceptionEnum exceptionEnum) {
        super(exceptionEnum.getDescription());
        this.exceptionEnum = exceptionEnum;
    }

    /**
     * 业务异常不创建完整调用栈，避免无意义的堆栈开销和日志噪声。
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
