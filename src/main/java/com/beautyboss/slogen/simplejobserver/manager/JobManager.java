package com.beautyboss.slogen.simplejobserver.manager;

import com.beautyboss.slogen.simplejobserver.base.ClusterJob;
import com.beautyboss.slogen.simplejobserver.lock.ClusterLock;
import com.beautyboss.slogen.simplejobserver.enums.ManageResultEnum;
import com.beautyboss.slogen.simplejobserver.enums.TriggerTypeEnum;
import com.beautyboss.slogen.simplejobserver.utils.IPUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
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
 * Date   : 2017/12/23
 */
@Component
public class JobManager {

    private static Logger logger = LoggerFactory.getLogger(JobManager.class);

    @Resource
    private Scheduler jobScheduler;

    @Resource
    private ClusterLock clusterLock;

    @PreDestroy
    public void clearRunningJobLocks() {
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

                String lockHost = clusterLock.getJobLockHost(job.jobName());
                if(StringUtils.isEmpty(lockHost) || IPUtils.getHostIP().equals(lockHost)) {
                    // 判断是不是本机上锁的，如果不是则不进行解锁操作
                    clusterLock.unlock(job.jobName());
                }

            }
            logger.info("Lock of running jobs are cleared.");
        } catch (Exception e) {
            logger.info("Failed to clean running tasks due to exception.");
        }
    }

    public List<JobExecutionContext> getRunningJobList() {
        try {
            return jobScheduler.getCurrentlyExecutingJobs();
        } catch (SchedulerException e) {
            logger.error("Failed to get running tasks list.error is {}.",e.getCause());
        }
        return new ArrayList<JobExecutionContext>();
    }

    public List<String> getRunningJobNameList() {
        List<String> jobNameList = new ArrayList<String>();
        for (JobExecutionContext context : getRunningJobList()) {
            JobKey jobKey = context.getJobDetail().getKey();
            if(null != jobKey) {
                jobNameList.add(jobKey.getName());
            }
        }
        return jobNameList;
    }

    public List<String> getJobList() {
        List<String> jobNames = new ArrayList<>();
        try {
            for(String groupName : jobScheduler.getJobGroupNames()) {
                for(JobKey jobKey : jobScheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    jobNames.add(jobKey.getName());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to get tasks list,error is {}.",e.getCause());
        }

        return jobNames;
    }

    public JobDetail getJobDetail(String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName,Scheduler.DEFAULT_GROUP);
        return jobScheduler.getJobDetail(jobKey);
    }

    public List<? extends Trigger> getJobTriggers(String jobName) throws SchedulerException {
        if(!hasJob(jobName)) {
            return null;
        }
        return jobScheduler.getTriggersOfJob(new JobKey(jobName,Scheduler.DEFAULT_GROUP));
    }

    public ManageResultEnum triggerJob(String jobName) throws SchedulerException {
        if(!hasJob(jobName)) {
            return ManageResultEnum.NON_EXISTED;
        }

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("TriggerType", TriggerTypeEnum.MANUAL);
        jobScheduler.triggerJob(new JobKey(jobName,Scheduler.DEFAULT_GROUP),jobDataMap);
        return ManageResultEnum.RUNNING;
    }

    public ManageResultEnum interruptJob(String jobName) throws UnableToInterruptJobException {
        if(!hasJob(jobName)) {
            return ManageResultEnum.NON_EXISTED;
        }
        jobScheduler.interrupt(new JobKey(jobName,Scheduler.DEFAULT_GROUP));
        return ManageResultEnum.INTERRUPTED;
    }

    public ManageResultEnum changonCron(String jobName,String cron) throws SchedulerException {
        return null;
    }

    public boolean hasJob(String jobName) {
        List<String> jobList = getJobList();
        return jobList.contains(jobName);
    }
}
