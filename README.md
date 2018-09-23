#### 一个简单的生产环境可用的分布式任务调度系统
#### 管理后台
`ip:9095/simplejob/jobs/admin`
![主页](http://ol76akva4.bkt.clouddn.com/jobserver.png)

#### 使用
继承`ClusterJob`类并实现`clusterExecute()`方法即可，使用`SimpleQuartzJob`注解
```Java
@SimpleQuartzJob(name = "myJob",cronExp = "0,10,20,30,40,50 10 * * * ?")
public class MyJob extends ClusterJob {
    @Override
    protected void clusterExecute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        System.out.println("Hello World");
    }

}
```

#### 原理
![原理](http://ol76akva4.bkt.clouddn.com/AmJobServer.png)
 
#### 链接
![Quartz分享](http://beautyboss.farbox.com/post/dts/quartzfen-xiang)