package com.beautyboss.slogen.simplejobserver.tasks;

import com.beautyboss.slogen.simplejobserver.base.ClusterJob;
import com.beautyboss.slogen.simplejobserver.register.AmQuartzJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Author : Slogen
 * Date   : 2017/12/17
 */
@AmQuartzJob(name = "myJob",cronExp = "0,10,20,30,40,50 10 * * * ?")
public class MyJob extends ClusterJob {
    @Override
    protected void clusterExecute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
//        System.out.println(String.format("jobDetail is %s", JSONUtils.toJSONString(jobDetail)));
    }

//    @Override
//    protected void registerComponent(JobExecutionContext context) {
//
//    }

}
