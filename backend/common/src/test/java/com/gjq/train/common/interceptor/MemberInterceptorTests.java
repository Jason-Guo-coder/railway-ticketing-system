package com.gjq.train.common.interceptor;

import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.resp.MemberLoginResp;
import com.gjq.train.common.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberInterceptorTests {

    private final MemberInterceptor interceptor = new MemberInterceptor();

    @AfterEach
    void tearDown() {
        LoginMemberContext.remove();
    }

    @Test
    void shouldExposeAndClearLoginMemberId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(
                "token",
                JwtUtil.createToken(1L, "13800001234", "test-secret")
        );

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(1L, LoginMemberContext.getId());
        MemberLoginResp member = LoginMemberContext.getMember();
        assertEquals("13800001234", member.getMobile());

        interceptor.afterCompletion(request, response, new Object(), null);
        assertNull(LoginMemberContext.getId());
    }
}
