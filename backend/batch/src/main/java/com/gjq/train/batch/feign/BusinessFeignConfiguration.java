package com.gjq.train.batch.feign;

import com.gjq.train.common.util.JwtUtil;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Business Feign客户端配置，负责为服务间请求携带管理员身份。
 */
public class BusinessFeignConfiguration {

    @Bean
    public RequestInterceptor businessAdminTokenInterceptor(
            @Value("${jwt.secret}") String jwtSecret
    ) {
        // 每次发起请求时生成短期管理员Token并写入请求头
        return template -> template.header(
                "token",
                JwtUtil.createAdminToken("batch-service", jwtSecret)
        );
    }
}
