package com.beautyboss.slogen.simplejobserver.web.controller;

import com.beautyboss.slogen.simplejobserver.base.ClusterJob;
import com.beautyboss.slogen.simplejobserver.base.JobSwitch;
import com.beautyboss.slogen.simplejobserver.enums.ManageResultEnum;
import com.beautyboss.slogen.simplejobserver.lock.ClusterLock;
import com.beautyboss.slogen.simplejobserver.manager.JobManager;
import com.beautyboss.slogen.simplejobserver.utils.SpringBeanUtils;
import com.beautyboss.slogen.simplejobserver.web.status.Response;
import com.beautyboss.slogen.simplejobserver.web.status.StatusCode;
import com.beautyboss.slogen.simplejobserver.web.vo.JobDetailVO;
import com.beautyboss.slogen.simplejobserver.web.vo.JobTriggerVO;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
@RestController
@RequestMapping("/amjob/jobs")
public class JobController {

    private static Logger logger = LoggerFactory.getLogger(JobController.class);

    @Resource
    private JobManager jobManager;

    @Resource
    private ClusterLock clusterLock;

    @Resource
    private JobSwitch jobSwitch;

    @RequestMapping("/run")
    public Response run(@RequestParam("jobName") String jobName) {
        if(jobManager.getRunningJobNameList().contains(jobName)) {
            String message = String.format("%s is running, won't trigger it.",jobName);
            return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
        }
        try {
            JobDetail jobDetail = jobManager.getJobDetail(jobName);
            if(null == jobDetail) {
                String message = String.format("%s cannot be found in tasks detail.", jobName);
                return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
            } else {
                Object bean = SpringBeanUtils.getBean(jobName);
                if(null == bean) {
                    String message = String.format("%s cannot be found in spring bean, won't trigger it.", jobName);
                    return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
                } else {
                    if(((ClusterJob) bean).switchOff()) {
                        String message = String.format("[%s] switch is off, would not execute.", jobName);
                        return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
                    }
                }
            }
        } catch (SchedulerException e) {
            return Response.failResponse(StatusCode.SERVICE_RUN_ERROR,e.getMessage());
        }

        try {
            if(clusterLock.isLocked(jobName)) {
                String message = String.format("%s is locked, perhaps runing on one of the server, please check log.", jobName);
                return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
            }
            ManageResultEnum manageResult = jobManager.triggerJob(jobName);
            String message = String.format("%s %s", jobName, manageResult.toString());
            return Response.success(StatusCode.SERVICE_RUN_SUCCESS,message);
        } catch (Exception e) {
            String message = Arrays.toString(e.getStackTrace());
            return Response.failResponse(StatusCode.SERVICE_RUN_ERROR,message);
        }
    }

    @RequestMapping("/kill")
    public Response kill(@RequestParam("jobName") String jobName) {
        if(!jobManager.getRunningJobNameList().contains(jobName)) {
            String message = String.format("%s is not ruinning,won't kill it.",jobName);
            return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
        }
        try {
            JobDetail jobDetail = jobManager.getJobDetail(jobName);
            if(null == jobDetail) {
                String message = String.format("%s cannnot be found in tasks detail",jobName);
                return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
            }
            Object bean = SpringBeanUtils.getBean(jobName);
            if(null == bean) {
                String message = String.format("%s cannot be found in spring bean,won't kill it ",jobName);
                return Response.failResponse(StatusCode.JOB_FORBIDDEN,message);
            }
        } catch (SchedulerException e) {
            return Response.failResponse(StatusCode.SERVICE_RUN_ERROR,e.getMessage());
        }

        try {
            ManageResultEnum manageResult = jobManager.interruptJob(jobName);
            String message = String.format("%s %s", jobName, manageResult.toString());
            return Response.success(StatusCode.SERVICE_RUN_SUCCESS,message);

        } catch (Exception e) {
            String message = Arrays.toString(e.getStackTrace());
            return Response.failResponse(StatusCode.SERVICE_RUN_ERROR,message);
        }
    }

    @RequestMapping("/lock")
    public Response lock(@RequestParam("jobName") String jobName) {
        clusterLock.lock(jobName);
        String message = String.format("%s locked",jobName);
        return Response.success(StatusCode.SERVICE_RUN_SUCCESS,message);
    }

    @RequestMapping("/unlock")
    public Response unlock(@RequestParam("jobName") String jobName) {
        clusterLock.unlock(jobName);
        String message = String.format("%s unlocked",jobName);
        return Response.success(StatusCode.SERVICE_RUN_SUCCESS,message);
    }

    @RequestMapping("/turnOff")
    public Response turnOff(@RequestParam("jobName") String jobName) {
        jobSwitch.turnOff(jobName);
        String message = String.format("%s turn off",jobName);
        return Response.success(StatusCode.SERVICE_RUN_SUCCESS,message);
    }

    @RequestMapping("/turnOn")
    public Response turnOn(@RequestParam("jobName") String jobName) {
        jobSwitch.turnOn(jobName);
        String message = String.format("%s turn on ",jobName);
        return Response.success(StatusCode.SERVICE_RUN_SUCCESS,message);
    }

    @RequestMapping("/list")
    public Response lists() {
        List<JobDetailVO> jobDetailVOList = new ArrayList<>();
        List<JobExecutionContext> runningJobs = jobManager.getRunningJobList();
        Map<String,JobExecutionContext> runningJobMap = new HashMap<>();
        for(JobExecutionContext context : runningJobs) {
            runningJobMap.put(context.getJobDetail().getKey().getName(),context);
        }

        for(String jobName : jobManager.getJobList()) {
            try {
                JobDetail jobDetail = jobManager.getJobDetail(jobName);

                JobDetailVO jobDetailVO = new JobDetailVO();
                BeanUtils.copyProperties(jobDetail, jobDetailVO);
                jobDetailVO.setJobClass(jobDetail.getJobClass());

                jobDetailVO.setSwitchOff(jobSwitch.isSwitchOff(jobName));

                if (runningJobMap.containsKey(jobName)) {
                    JobExecutionContext executionContext = runningJobMap.get(jobName);
                    jobDetailVO.setStatus("RUNNING");
                    jobDetailVO.setRunTime(executionContext.getJobRunTime());
                    jobDetailVO.setFireTime(executionContext.getFireTime());
                }

                List<JobTriggerVO> triggerVOList = new ArrayList<JobTriggerVO>();
                List<? extends Trigger> triggersOfJob = jobManager.getJobTriggers(jobName);
                for (Trigger trigger : triggersOfJob) {
                    JobTriggerVO jobTriggerVO = new JobTriggerVO();
                    BeanUtils.copyProperties(trigger, jobTriggerVO);
                    triggerVOList.add(jobTriggerVO);
                }
                jobDetailVO.setTriggerList(triggerVOList);

                jobDetailVOList.add(jobDetailVO);
            } catch (SchedulerException e) {
                logger.error("Failed to get tasks detail for {},Exception is {}.", jobName,e.getMessage());
                return Response.failResponse(StatusCode.SERVICE_RUN_ERROR,e.getMessage());
            }
        }

        return Response.success(StatusCode.SERVICE_RUN_SUCCESS,jobDetailVOList);
    }

}
