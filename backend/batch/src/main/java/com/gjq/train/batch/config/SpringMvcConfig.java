package com.gjq.train.batch.config;

import com.gjq.train.batch.interceptor.AdminInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Batch模块的Spring MVC配置，统一保护管理员调度接口。
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    // 管理员身份校验拦截器
    @Resource
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 所有/admin路径都必须通过管理员Token校验
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**");
    }
}
