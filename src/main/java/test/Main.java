package test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobBuilder.newJob;

/**
 * Author : Slogen
 * Date   : 2018/5/15
 */
public class Main {

    public static void main(String[] args) throws SchedulerException, InterruptedException {

        // 1. 创建Scheduler
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2. 创建JobDetail
        JobDetail jobDetail = newJob(HelloJob.class) // 定义job类真正的执行类
                .withIdentity("JobName","JobGroup") // 定义name/group
                .usingJobData("param","Hello World") // 定义属性，保存在JobDataMap中
                .build();

        // 3. 创建Trigger,定义触发规则
        Trigger trigger = newTrigger()
                .withIdentity("CronTriggerName","CronTriggerGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?")) //  cron表达式，每5s执行一次
                .startNow() // 一旦加入scheduler，立即生效
                .build();

        // 4. 把job和trigger注册到Scheduler中
        scheduler.scheduleJob(jobDetail,trigger);

        // 5. 启动调度器
        scheduler.start();

        Thread.sleep(100000);

        // 6. 停止调度
//        scheduler.s();
    }
}
