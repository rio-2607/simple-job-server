#### 一个简单的生产环境可用的分布式任务调度系统
#### 管理后台
`ip:9095/simplejob/jobs/admin`
![主页](https://i.loli.net/2019/02/08/5c5d477bbe2ae.png)

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
![原理](https://i.loli.net/2019/02/08/5c5d477b928a3.png)
 
#### 链接
[Quartz分享](http://beautyboss.me/2019/03/29/Quartz%E5%88%86%E4%BA%AB/)
