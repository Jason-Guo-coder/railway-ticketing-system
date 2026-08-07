package com.gjq.train.batch.feign;

import cn.hutool.jwt.JWTUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessFeignConfigurationTests {

    private static final String SECRET = "batch-feign-test-secret";

    @Test
    void shouldAddValidAdminToken() {
        BusinessFeignConfiguration configuration =
                new BusinessFeignConfiguration();
        RequestInterceptor interceptor =
                configuration.businessAdminTokenInterceptor(SECRET);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        String token = template.headers().get("token").iterator().next();
        assertNotNull(token);
        assertTrue(
                JWTUtil.parseToken(token)
                        .setKey(SECRET.getBytes(StandardCharsets.UTF_8))
                        .validate(0)
        );
    }
}
