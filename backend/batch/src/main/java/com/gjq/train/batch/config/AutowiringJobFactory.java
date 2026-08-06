package com.gjq.train.batch.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * Quartz任务实例工厂，使Quartz创建的Job也能注入Spring Bean。
 */
@Component
public class AutowiringJobFactory extends SpringBeanJobFactory {

    // Spring提供的Bean自动装配工厂
    private final AutowireCapableBeanFactory beanFactory;

    public AutowiringJobFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //1. 由Quartz父类创建任务实例
        Object jobInstance = super.createJobInstance(bundle);
        //2. 为任务实例注入Spring容器中的依赖
        beanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
