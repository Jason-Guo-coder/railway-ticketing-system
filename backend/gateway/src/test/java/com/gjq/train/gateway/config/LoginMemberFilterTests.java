package com.gjq.train.gateway.config;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginMemberFilterTests {

    private static final String SECRET = "gateway-filter-test-secret";

    private final LoginMemberFilter filter =
            new LoginMemberFilter(SECRET);

    @Test
    void shouldAllowLoginWithoutToken() {
        MockServerWebExchange exchange = exchange(
                MockServerHttpRequest.post("/member/member/login").build()
        );
        AtomicBoolean called = new AtomicBoolean();

        filter.filter(exchange, successfulChain(called)).block();

        assertTrue(called.get());
    }

    @Test
    void shouldRejectProtectedRequestWithoutToken() {
        MockServerWebExchange exchange = exchange(
                MockServerHttpRequest.get("/member/member/count").build()
        );
        AtomicBoolean called = new AtomicBoolean();

        filter.filter(exchange, successfulChain(called)).block();

        assertFalse(called.get());
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );
        assertEquals(
                "{\"success\":false,"
                        + "\"message\":\"登录已失效，请重新登录\","
                        + "\"content\":null}",
                exchange.getResponse().getBodyAsString().block()
        );
    }

    @Test
    void shouldAllowProtectedRequestWithValidToken() {
        MockServerWebExchange exchange = exchange(
                MockServerHttpRequest
                        .get("/member/member/count")
                        .header("token", createToken(1))
                        .build()
        );
        AtomicBoolean called = new AtomicBoolean();

        filter.filter(exchange, successfulChain(called)).block();

        assertTrue(called.get());
    }

    @Test
    void shouldRejectInvalidToken() {
        MockServerWebExchange exchange = exchange(
                MockServerHttpRequest
                        .get("/member/member/count")
                        .header("token", "invalid-token")
                        .build()
        );
        AtomicBoolean called = new AtomicBoolean();

        filter.filter(exchange, successfulChain(called)).block();

        assertFalse(called.get());
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );
    }

    @Test
    void shouldRejectExpiredToken() {
        MockServerWebExchange exchange = exchange(
                MockServerHttpRequest
                        .get("/member/member/count")
                        .header("token", createToken(-1))
                        .build()
        );
        AtomicBoolean called = new AtomicBoolean();

        filter.filter(exchange, successfulChain(called)).block();

        assertFalse(called.get());
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );
    }

    private MockServerWebExchange exchange(MockServerHttpRequest request) {
        return MockServerWebExchange.from(request);
    }

    private GatewayFilterChain successfulChain(AtomicBoolean called) {
        return exchange -> {
            called.set(true);
            return Mono.empty();
        };
    }

    private String createToken(int validHours) {
        DateTime now = DateTime.now();
        Map<String, Object> payload = new HashMap<>();
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JWTPayload.NOT_BEFORE, now);
        payload.put(
                JWTPayload.EXPIRES_AT,
                now.offsetNew(DateField.HOUR, validHours)
        );
        payload.put("id", 1L);
        payload.put("mobile", "13900001234");
        return JWTUtil.createToken(
                payload,
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }
}
