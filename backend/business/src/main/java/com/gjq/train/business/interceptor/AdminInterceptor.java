package com.gjq.train.business.interceptor;

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

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "token";

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
        //1. 获取管理员Token，未携带时直接拒绝请求
        String token = request.getHeader(TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            return unauthorized(response);
        }

        //2. 校验Token签名、有效期和管理员角色
        try {
            JWT jwt = JWTUtil.parseToken(token).setKey(tokenKey);
            Object role = jwt.getPayload("role");
            if (jwt.validate(0) && "admin".equals(role)) {
                return true;
            }
        } catch (RuntimeException ignored) {
            return unauthorized(response);
        }

        //3. 普通会员Token或无效Token均不能访问管理接口
        return unauthorized(response);
    }

    private boolean unauthorized(HttpServletResponse response)
            throws IOException {
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
