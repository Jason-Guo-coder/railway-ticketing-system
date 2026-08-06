package com.gjq.train.batch.interceptor;

import com.gjq.train.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 管理员拦截器测试，验证管理员Token和缺少Token两种情况。
 */
class AdminInterceptorTests {

    private static final String SECRET = "batch-interceptor-test-secret";

    private final AdminInterceptor interceptor = new AdminInterceptor(SECRET);

    @Test
    void shouldAllowAdminToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                "token",
                JwtUtil.createAdminToken("admin", SECRET)
        );

        assertTrue(interceptor.preHandle(
                request,
                new MockHttpServletResponse(),
                new Object()
        ));
    }

    @Test
    void shouldRejectMissingToken() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(
                new MockHttpServletRequest(),
                response,
                new Object()
        ));
        assertEquals(401, response.getStatus());
    }
}
