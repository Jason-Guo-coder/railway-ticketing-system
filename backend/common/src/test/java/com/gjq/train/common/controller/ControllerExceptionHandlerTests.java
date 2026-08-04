package com.gjq.train.common.controller;

import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ControllerExceptionHandlerTests {

    private final ControllerExceptionHandler handler =
            new ControllerExceptionHandler();

    @Test
    void shouldReturnBusinessExceptionDescription() {
        BusinessException exception = new BusinessException(
                BusinessExceptionEnum.MEMBER_MOBILE_EXIST
        );

        ResponseEntity<Result<Void>> response =
                handler.businessExceptionHandler(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "手机号已经注册",
                response.getBody().getMessage()
        );
        assertEquals(0, exception.getStackTrace().length);
    }

    @Test
    void shouldHideSystemExceptionDetails() {
        ResponseEntity<Result<Void>> response =
                handler.exceptionHandler(
                        new RuntimeException("数据库连接内部信息")
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "系统异常，请联系管理员",
                response.getBody().getMessage()
        );
    }
}
