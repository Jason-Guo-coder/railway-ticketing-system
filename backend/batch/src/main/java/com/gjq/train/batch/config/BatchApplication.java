package com.gjq.train.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * Batch模块启动类，负责启动Quartz任务调度服务和管理接口。
 */
@SpringBootApplication(scanBasePackages = "com.gjq.train")
public class BatchApplication {

    // Batch模块日志记录器
    private static final Logger LOG = LoggerFactory.getLogger(BatchApplication.class);

    public static void main(String[] args) {
        //1. 启动Spring Boot应用
        SpringApplication app = new SpringApplication(BatchApplication.class);
        Environment env = app.run(args).getEnvironment();
        //2. 输出Batch模块访问地址
        LOG.info("启动成功！！！");
        LOG.info("地址：\thttp://127.0.0.1:{}{}",
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"));
    }
}
