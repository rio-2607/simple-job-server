package com.beautyboss.slogen.simplejobserver.register;

import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.text.ParseException;

/**
 * Author : Slogen
 * Date   : 2017/12/11
 */
public abstract class BaseRegister implements ApplicationContextAware,ApplicationListener{

    protected static ApplicationContext applicationContext;
    protected static Scheduler jobScheduler;

    protected static void initScheduler() {
        if(null == jobScheduler) {
            jobScheduler = (Scheduler) applicationContext.getBean("jobScheduler");
            try {
                jobScheduler.start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
    protected static void createAndRegisterJob(Class jobClass, String jobName, String jobGroup,
                                               String cronExpression, JobDataMap jobDataMap) throws SchedulerException, ParseException {
        // create tasks detail
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName,jobGroup)
//                .setJobData(jobDataMap)
                .build();
        // create trigger
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "Trigger",jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .forJob(jobDetail)
                .build();

        jobScheduler.scheduleJob(jobDetail,cronTrigger);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        initScheduler();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BaseRegister.applicationContext = applicationContext;
    }
}
