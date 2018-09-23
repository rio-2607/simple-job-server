package test;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * Author : Slogen
 * Date   : 2018/5/15
 */
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        String param = jobDetail.getJobDataMap().getString("param"); // 从JobDataMap中获取数据
        System.out.println(">>>>>>>> param:" + param);
    }

}
