package com.gjq.train.batch.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Batch启动测试，确认Quartz、数据源和MyBatis基础设施能够装配。
 */
@SpringBootTest
class BatchApplicationTests {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void contextLoads() {
        assertNotNull(scheduler);
        assertNotNull(dataSource);
        assertNotNull(sqlSessionFactory);
    }
}
