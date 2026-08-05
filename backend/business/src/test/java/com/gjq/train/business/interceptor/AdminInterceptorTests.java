package com.gjq.train.business.interceptor;

import com.gjq.train.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminInterceptorTests {

    private static final String SECRET = "admin-interceptor-test-secret";

    private final AdminInterceptor interceptor =
            new AdminInterceptor(SECRET);

    @Test
    void shouldAllowAdminToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                "token",
                JwtUtil.createAdminToken("admin", SECRET)
        );

        boolean allowed = interceptor.preHandle(
                request,
                new MockHttpServletResponse(),
                new Object()
        );

        assertTrue(allowed);
    }

    @Test
    void shouldRejectMemberToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(
                "token",
                JwtUtil.createToken(1L, "13900001234", SECRET)
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(
                request,
                response,
                new Object()
        );

        assertFalse(allowed);
        assertEquals(401, response.getStatus());
        assertEquals(
                "{\"success\":false,"
                        + "\"message\":\"管理员登录已失效，请重新登录\","
                        + "\"content\":null}",
                response.getContentAsString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(
                new MockHttpServletRequest(),
                response,
                new Object()
        );

        assertFalse(allowed);
        assertEquals(401, response.getStatus());
    }
}
