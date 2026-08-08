package com.gjq.train.business.config;

import com.gjq.train.business.interceptor.AdminInterceptor;
import com.gjq.train.common.interceptor.MemberInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.util.AntPathMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringMvcConfigTests {

    @Test
    void shouldRegisterMemberInterceptorOutsideAdminRoutes() {
        SpringMvcConfig config = new SpringMvcConfig();
        AdminInterceptor adminInterceptor =
                new AdminInterceptor("test-secret");
        MemberInterceptor memberInterceptor = new MemberInterceptor();
        ReflectionTestUtils.setField(
                config,
                "adminInterceptor",
                adminInterceptor
        );
        ReflectionTestUtils.setField(
                config,
                "memberInterceptor",
                memberInterceptor
        );

        ExposedInterceptorRegistry registry =
                new ExposedInterceptorRegistry();
        config.addInterceptors(registry);

        List<MappedInterceptor> interceptors = registry.interceptors()
                .stream()
                .map(item -> (MappedInterceptor) item)
                .toList();
        MappedInterceptor member = findInterceptor(
                interceptors,
                memberInterceptor
        );
        assertTrue(member.matches("/confirm-order/do", new AntPathMatcher()));
        assertFalse(member.matches(
                "/admin/confirm-order/query-list",
                new AntPathMatcher()
        ));
    }

    private MappedInterceptor findInterceptor(
            List<MappedInterceptor> interceptors,
            HandlerInterceptor target
    ) {
        return interceptors.stream()
                .filter(item -> item.getInterceptor() == target)
                .findFirst()
                .orElseThrow();
    }

    private static class ExposedInterceptorRegistry
            extends InterceptorRegistry {

        List<Object> interceptors() {
            return getInterceptors();
        }
    }
}
