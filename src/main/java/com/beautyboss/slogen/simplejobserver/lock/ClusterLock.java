package com.beautyboss.slogen.simplejobserver.lock;

import com.beautyboss.slogen.simplejobserver.mapper.SimpleJobLockMapper;
import com.beautyboss.slogen.simplejobserver.base.ClusterJob;
import com.beautyboss.slogen.simplejobserver.enums.JobLockStatusEnum;
import com.beautyboss.slogen.simplejobserver.utils.IPUtils;
import com.beautyboss.slogen.simplejobserver.utils.JobNameHashUtil;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
@Component("clusterLock")
public class ClusterLock implements Lock {

    private static final Logger logger = LoggerFactory.getLogger(ClusterLock.class);

    @Resource
    private SimpleJobLockMapper amJobLockDao;

    @Resource
    private Scheduler scheduler;

    public ClusterLock() {
        Runtime.getRuntime().addShutdownHook(new ClusterLockHockThread());
    }


    @Override
    public boolean lock(String jobName) {
        try {
            int jobHash = JobNameHashUtil.SDBMHash16(jobName);
            amJobLockDao.insertJobLock(jobName, jobHash, JobLockStatusEnum.NEW.getStatus(), IPUtils.getHostIP());
            int updateRet = amJobLockDao.updateStatus(jobHash, JobLockStatusEnum.PROCESSING.getStatus(),
                    JobLockStatusEnum.NEW.getStatus());

            if(updateRet > 0) {
                logger.info( "Cluster Job: {} add tasks lock in DB.", jobName);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.warn("The Cluster Job: {} is running on other cluster, execute ignored.", jobName);
            logger.debug( "The Cluster Job: {} is running on other cluster, execute ignored.", jobName);
        }
        return false;
    }

    @Override
    public void unlock(String jobName) {
        try {
            int jobHash = JobNameHashUtil.SDBMHash16(jobName);
            amJobLockDao.updateStatus(jobHash, JobLockStatusEnum.NEW.getStatus(),
                    JobLockStatusEnum.PROCESSING.getStatus());
            logger.info( "Cluster Job: {} tasks lock removed.", jobName);
        } catch (Exception e) {
            logger.error(  "Failed to remove tasks lock for {}", jobName);
        }
    }

    @Override
    public boolean isLocked(String jobName) {
        int jobHash = JobNameHashUtil.SDBMHash16(jobName);
        Integer jobStatus = amJobLockDao.getJobLockStatus(jobHash);
        if(null != jobStatus && JobLockStatusEnum.PROCESSING.getStatus() == jobStatus) {
            logger.info("Cluster Job: {} tasks has been locked in DB.", jobName);
            return true;
        } else {
            logger.info("Cluster Job: {} tasks is not locked in DB.", jobName);
            return false;
        }
    }

    @PreDestroy
    public void cleanLockByPreDestroy() {
        logger.info("PreDestroy is cleaning tasks lock.");
        clearRunningJobLocks();
    }

    private void clearRunningJobLocks() {
        try {
            logger.info("Spring is going to shutdown, start to clean lock of running jobs.");
            List<JobExecutionContext> runningJobs = getRunningJobList();

            // do some clean code
            for(JobExecutionContext context : runningJobs) {
                ClusterJob job = (ClusterJob) context.getJobInstance();
                try {
                    logger.info("Try to clean tasks {} when shutdown.",job.jobName());
                    job.interrupt();
                } catch (UnableToInterruptJobException e) {
                    logger.error("Failed to clean tasks {} when shutdown.",job.jobName());
                }
                String host = getJobLockHost(job.jobName());
                if(StringUtils.isEmpty(host) || IPUtils.getHostIP().equals(host)) {
                    unlock(job.jobName());
                }

            }
            logger.info("Lock of running jobs are cleared.");
        } catch (Exception e) {
            logger.info("Failed to clean running tasks due to exception.");
        }
    }

    private List<JobExecutionContext> getRunningJobList() {
        try {
            return scheduler.getCurrentlyExecutingJobs();
        } catch (SchedulerException e) {
            logger.error("Failed to get running tasks list.error is {}.",e.getCause());
        }
        return new ArrayList<JobExecutionContext>();
    }

    private class ClusterLockHockThread extends Thread {
        @Override
        public void run() {
            logger.info("Shutdown hock is cleaning tasks lock.");
            clearRunningJobLocks();
        }
    }

    public String getJobLockHost(String jobName) {
        int jobHash = JobNameHashUtil.SDBMHash16(jobName);
        return amJobLockDao.getJobLockHost(jobHash);
    }
}
