package com.beautyboss.slogen.simplejobserver.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Author : Slogen
 * Date   : 2017/12/17
 */
@Configuration
public class QuartzConfig {

    @Bean("jobScheduler")
    @Primary
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        return scheduler;
    }

}
