package com.gjq.train.common.controller;

import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 项目统一异常处理器。
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 业务异常只记录枚举信息，并将业务描述返回给前端。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> businessExceptionHandler(
            BusinessException exception) {
        BusinessExceptionEnum exceptionEnum = exception.getExceptionEnum();
        LOG.warn(
                "业务异常：code={}, description={}",
                exceptionEnum.getCode(),
                exceptionEnum.getDescription()
        );

        return ResponseEntity.badRequest()
                .body(Result.fail(exceptionEnum.getDescription()));
    }

    /**
     * 请求体为空或 JSON 格式错误时返回统一提示。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> requestBodyExceptionHandler() {
        String message = "请求参数不能为空或格式错误";
        LOG.warn(message);

        return ResponseEntity.badRequest()
                .body(Result.fail(message));
    }

    /**
     * 参数校验失败时返回校验注解中定义的提示。
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> validationExceptionHandler(
            BindException exception) {
        String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(item -> item != null && !item.isBlank())
                .findFirst()
                .orElse("请求参数校验失败");
        LOG.warn("参数校验失败：{}", message);

        return ResponseEntity.badRequest()
                .body(Result.fail(message));
    }

    /**
     * 捕获未在业务代码中处理的异常，并返回统一失败结果。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> exceptionHandler(
            Exception exception) {
        LOG.error("系统异常", exception);

        HttpStatusCode status = exception instanceof ErrorResponse errorResponse
                ? errorResponse.getStatusCode()
                : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = exception instanceof ErrorResponse
                ? exception.getMessage()
                : "系统异常，请联系管理员";

        return ResponseEntity.status(status)
                .body(Result.fail(message));
    }
}
