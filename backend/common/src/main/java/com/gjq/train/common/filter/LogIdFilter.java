package com.gjq.train.common.filter;

import cn.hutool.core.util.RandomUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 为一次 HTTP 请求中的所有日志设置同一个日志流水号。
 */
@Component
public class LogIdFilter extends OncePerRequestFilter {

    static final String LOG_ID = "LOG_ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String logId = System.currentTimeMillis()
                + RandomUtil.randomString(3);
        MDC.put(LOG_ID, logId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(LOG_ID);
        }
    }
}
