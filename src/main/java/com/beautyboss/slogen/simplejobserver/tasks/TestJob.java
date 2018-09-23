package com.beautyboss.slogen.simplejobserver.tasks;

import com.beautyboss.slogen.simplejobserver.base.ClusterJob;
import com.beautyboss.slogen.simplejobserver.register.SimpleQuartzJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
@SimpleQuartzJob(name = "testJob",cronExp = "0,10,20,30,40,50 * * * * ?")
public class TestJob extends ClusterJob {

    @Override
    protected void clusterExecute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(">>>>>>>> Hello World");
    }


}
