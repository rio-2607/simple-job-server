package com.beautyboss.slogen.simplejobserver.base;

import com.beautyboss.slogen.simplejobserver.enums.TaskTypeEnum;
import com.beautyboss.slogen.simplejobserver.lock.ClusterLock;
import com.beautyboss.slogen.simplejobserver.register.AmQuartzJob;
import com.beautyboss.slogen.simplejobserver.utils.DateUtils;
import com.beautyboss.slogen.simplejobserver.data.SimpleJobLog;
import com.beautyboss.slogen.simplejobserver.mapper.SimpleJobLogMapper;
import com.beautyboss.slogen.simplejobserver.register.AnnoJobRegisterEvent;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author : Slogen
 * Date   : 2017/12/11
 */
public abstract class ClusterJob extends QuartzJobBean implements StatefulJob,InterruptableJob,ApplicationContextAware,InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(ClusterJob.class);

    private static SimpleJobLogMapper simpleJobLogMapper;
    private static ClusterLock clusterLock;
    private static JobSwitch jobSwitch;
    private static Random random;
    private static ApplicationContext applicationContext;
    private volatile SimpleJobLog simpleJobLog;
    private volatile boolean interrupted = false;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);


    private void init() {
        if (null == simpleJobLogMapper) simpleJobLogMapper = (SimpleJobLogMapper) applicationContext.getBean("simpleJobLogMapper");
        if (null == clusterLock) clusterLock = (ClusterLock) applicationContext.getBean("clusterLock");
        if (null == jobSwitch) jobSwitch = (JobSwitch) applicationContext.getBean("jobSwitch");
        if (null == random) random = new Random();
    }

    private void init(JobExecutionContext context) {
        init();
//        registerComponent(context);
    }

    protected abstract void clusterExecute(JobExecutionContext context) throws JobExecutionException;

//    protected abstract void registerComponent(JobExecutionContext context);



    public String jobName() {
        AmQuartzJob amQuartzJob = AnnotationUtils.findAnnotation(this.getClass(),AmQuartzJob.class);
        if(null == amQuartzJob) {
            return this.getClass().getSimpleName();
        }
        return amQuartzJob.name();
    }

    private TaskTypeEnum jobType() {
        AmQuartzJob amQuartzJob = AnnotationUtils.findAnnotation(this.getClass(),AmQuartzJob.class);
        if(null == amQuartzJob) {
            // 没有的话默认为分布式需要抢锁的任务
            return TaskTypeEnum.SINGLE;
        }
        String type = amQuartzJob.type();
        if(TaskTypeEnum.ALL.getType().equals(type)) {
            return TaskTypeEnum.ALL;
        }
        return TaskTypeEnum.SINGLE;
    }


    @Override
    public void interrupt() throws UnableToInterruptJobException {
        logger.warn("Cluster Job: {} is interrupting manually.", jobName());
        interrupted = true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        applicationContext.publishEvent(new AnnoJobRegisterEvent(this));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        init(jobExecutionContext);

        atomicInteger.incrementAndGet();
        try {
            //switch
            if (switchOff()) {
                return;
            }
            // 判断任务类型
            if(TaskTypeEnum.ALL == jobType()) {
                // 如果是ALL类型的则不需要去抢锁
                working(jobExecutionContext);
            } else {
                // SINGLE类型的则需要去抢锁
                // 随机休眠几秒
                randomSleep();
                if (!lockSuccess(jobExecutionContext)) {
                    logger.info("jobName:[{}] locked failed,maybe the lock has been locked.",jobName());
                    return;
                }
                //do work
                working(jobExecutionContext);
                //sleep 5s
                postSleep();
                //unlock
                unlock();
            }
        } finally {
            atomicInteger.decrementAndGet();
            logger.info( "Cluster Job: {} finished.", jobName());
        }
    }

    public boolean switchOff() {
        boolean isSwitchOff = jobSwitch.isSwitchOff(jobName());
        if (isSwitchOff) {
            logger.info( String.format("%s switch is off, would not execute.", jobName()));
        }
        return isSwitchOff;
    }

    private void randomSleep() {
        try {
            int currentRunning = atomicInteger.get();
            logger.debug( "Cluster Job: {} ,there's %d jobs are running.", jobName(), currentRunning);

            TimeUnit.MILLISECONDS.sleep(random.nextInt() % 20 + 5 * (currentRunning > 0 ? currentRunning : 0));
        } catch (InterruptedException e) {
            logger.debug("Cluster Job: {} got exception during working.", jobName());
        }
    }

    /**
     * 防止Job锁立即释放，其他机器再次获得锁执行
     */
    private void postSleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error("Cluster Job postSleep Exception:", e);
        }
    }

    private void working(JobExecutionContext context) {
        try {
            JobWorker jobWorker = new JobWorker(context);
            jobWorker.start();

            while (true) {
                if (!jobWorker.isAlive()) {
                    logger.debug("Cluster Job: {} completed normally.", jobName());
                    break;
                }
                if (interrupted) {
                    //really bad implementation!
                    jobWorker.suspend();
                    jobWorker.stop();
                    logger.debug("Cluster Job: {} completed by interruption.", jobName());
                    break;
                }
            }
        } catch (Exception e) {
            logger.error( "Cluster Job: {} got exception during working.", jobName());
        }
    }

    private boolean lockSuccess(JobExecutionContext context) {
        return clusterLock.lock(jobName());
    }

    private void unlock() {
        clusterLock.unlock(jobName());
    }

    private void startLogging() {
        simpleJobLog = new SimpleJobLog();
        String startedTime = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        simpleJobLog.setJobName(jobName());
        simpleJobLog.setStartedTime(startedTime);
        String host = "";

        try {
            InetAddress addr = InetAddress.getLocalHost();
            host = addr.getHostName();
        } catch (UnknownHostException e) {
            logger.error( "Cluster Job:{} startLogging Exception:", jobName());
        }

        simpleJobLog.setHost(host);

        int id = simpleJobLogMapper.addOneJobLog(simpleJobLog);
        simpleJobLog.setId(id);
    }

    private void endLogging() {
        String finishedTime = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        simpleJobLog.setFinishedTime(finishedTime);
        simpleJobLog.setMessage("success");
        simpleJobLogMapper.updateOneJobLogFinishedTime(simpleJobLog);
    }

    private void exceptionLogging(Exception e) {
        simpleJobLog.setMessage(e.toString());
        simpleJobLogMapper.updateOneJobLogExceptionMessage(simpleJobLog);
    }

    private class JobWorker extends Thread {

        private JobExecutionContext context;

        private JobWorker(JobExecutionContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            try {
                startLogging();
                clusterExecute(context);
                endLogging();
            } catch (Exception e) {
                exceptionLogging(e);
                logger.error( "Cluster Job: {} failed due to exception.", jobName());
            }
        }
    }
}
