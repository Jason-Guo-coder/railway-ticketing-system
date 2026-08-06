package com.gjq.train.batch.config;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz调度器配置，补充Spring Boot自动配置未包含的任务工厂设置。
 */
@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer(
            AutowiringJobFactory jobFactory
    ) {
        // 设置任务工厂，并让调度器在应用启动后延迟2秒开始工作
        return factory -> {
            factory.setJobFactory(jobFactory);
            factory.setStartupDelay(2);
        };
    }
}
