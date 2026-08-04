package com.gjq.train.gateway.config;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class LoginMemberFilter implements GlobalFilter, Ordered {

    private static final Logger LOG =
            LoggerFactory.getLogger(LoginMemberFilter.class);

    private static final String TOKEN_HEADER = "token";

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/member/member/login",
            "/member/member/register"
    );

    private static final byte[] UNAUTHORIZED_BODY = (
            "{\"success\":false,"
                    + "\"message\":\"登录已失效，请重新登录\","
                    + "\"content\":null}"
    ).getBytes(StandardCharsets.UTF_8);

    private final byte[] tokenKey;

    public LoginMemberFilter(@Value("${jwt.secret}") String jwtSecret) {
        this.tokenKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // 登录、验证码和跨域预检请求不需要校验Token
        if (HttpMethod.OPTIONS.equals(method)
                || PUBLIC_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest()
                .getHeaders()
                .getFirst(TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            LOG.warn("请求缺少Token：{}", path);
            return unauthorized(exchange);
        }

        // 同时校验JWT签名、生效时间和过期时间
        try {
            JWT jwt = JWTUtil.parseToken(token).setKey(tokenKey);
            if (jwt.validate(0)) {
                return chain.filter(exchange);
            }
        } catch (RuntimeException ignored) {
            LOG.warn("Token无效或已过期：{}", path);
            return unauthorized(exchange);
        }

        LOG.warn("Token无效或已过期：{}", path);
        return unauthorized(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(
                MediaType.APPLICATION_JSON
        );
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(UNAUTHORIZED_BODY);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
