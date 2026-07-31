package com.gjq.train.common.filter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogIdFilterTests {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void shouldKeepLogIdForWholeRequestAndClearItAfterward()
            throws Exception {
        LogIdFilter filter = new LogIdFilter();
        AtomicReference<String> logIdDuringRequest =
                new AtomicReference<>();

        filter.doFilter(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                (request, response) -> logIdDuringRequest.set(
                        MDC.get(LogIdFilter.LOG_ID)
                )
        );

        assertNotNull(logIdDuringRequest.get());
        assertNull(MDC.get(LogIdFilter.LOG_ID));
    }
}
