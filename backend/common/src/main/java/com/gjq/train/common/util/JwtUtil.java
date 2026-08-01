package com.gjq.train.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class JwtUtil {

    private JwtUtil() {
    }

    public static String createToken(
            Long id,
            String mobile,
            String secret
    ) {
        DateTime now = DateTime.now();
        Map<String, Object> payload = new HashMap<>();

        // 设置签发、生效和过期时间
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JWTPayload.NOT_BEFORE, now);
        payload.put(
                JWTPayload.EXPIRES_AT,
                now.offsetNew(DateField.HOUR, 1)
        );

        // 保存当前登录会员信息
        payload.put("id", id);
        payload.put("mobile", mobile);
        return JWTUtil.createToken(
                payload,
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}
