package com.gjq.train.business.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@MapperScan({
        "com.gjq.train.business.station.mapper",
        "com.gjq.train.business.train.mapper",
        "com.gjq.train.business.trainstation.mapper",
        "com.gjq.train.business.traincarriage.mapper",
        "com.gjq.train.business.trainseat.mapper",
        "com.gjq.train.business.dailytrain.mapper",
        "com.gjq.train.business.dailytrainstation.mapper",
        "com.gjq.train.business.dailytraincarriage.mapper",
        "com.gjq.train.business.dailytrainseat.mapper",
        "com.gjq.train.business.dailytrainticket.mapper",
        "com.gjq.train.business.confirmorder.mapper",
        "com.gjq.train.business.memberticket.mapper"
})
@SpringBootApplication(scanBasePackages = "com.gjq.train")
public class BusinessApplication {

    private static final Logger LOG =
            LoggerFactory.getLogger(BusinessApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！！");
        LOG.info("地址：\thttp://127.0.0.1:{}{}",
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"));
    }
}
