package com.gjq.train.batch.interceptor;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Batch管理员拦截器，避免普通会员操作Quartz调度任务。
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    // 项目约定的JWT请求头名称
    private static final String TOKEN_HEADER = "token";

    // JWT签名校验密钥
    private final byte[] tokenKey;

    public AdminInterceptor(@Value("${jwt.secret}") String jwtSecret) {
        this.tokenKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        //1. 获取管理员Token
        String token = request.getHeader(TOKEN_HEADER);
        //① 未携带Token时直接拒绝请求
        if (!StringUtils.hasText(token)) {
            return unauthorized(response);
        }

        //2. 校验Token签名、有效期和管理员角色
        try {
            JWT jwt = JWTUtil.parseToken(token).setKey(tokenKey);
            Object role = jwt.getPayload("role");
            //① 只有有效的管理员Token才能继续访问
            if (jwt.validate(0) && "admin".equals(role)) {
                return true;
            }
        } catch (RuntimeException ignored) {
            //② Token无法解析时返回统一的未登录响应
            return unauthorized(response);
        }

        //3. 普通会员Token或无效Token不能访问调度管理接口
        return unauthorized(response);
    }

    private boolean unauthorized(HttpServletResponse response)
            throws IOException {
        // 统一返回管理员登录失效响应
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"success\":false,"
                        + "\"message\":\"管理员登录已失效，请重新登录\","
                        + "\"content\":null}"
        );
        return false;
    }
}
