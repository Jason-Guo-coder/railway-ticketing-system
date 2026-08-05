package com.gjq.train.business.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BusinessApplicationTests {

    @Autowired
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    @Test
    void contextLoads() {
        assertNotNull(mybatisPlusInterceptor);
    }
}
