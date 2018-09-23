package com.beautyboss.slogen.simplejobserver.register;

import com.alibaba.druid.support.json.JSONUtils;
import com.beautyboss.slogen.simplejobserver.base.ClusterJob;
import com.beautyboss.slogen.simplejobserver.enums.RegisterResultEnum;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * Author : Slogen
 * Date   : 2017/12/11
 */
@Component("quartzJobAnnoRegister")
public class AnnoJobRegister extends BaseRegister{

    private static final Logger logger = LoggerFactory.getLogger(AnnoJobRegister.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        super.onApplicationEvent(event);
        if (event instanceof AnnoJobRegisterEvent) {
            instant((ClusterJob) event.getSource());
        }
    }

    private RegisterResultEnum instant(ClusterJob bean) throws BeansException {
        Class<? extends ClusterJob> jobClass = bean.getClass();

        SimpleQuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(jobClass,SimpleQuartzJob.class);
        if(null == quartzJobAnnotation) {
            return RegisterResultEnum.IGNORED;
        }

        initScheduler();

        try {
            String jobName = quartzJobAnnotation.name();
            String jobGroup = quartzJobAnnotation.group();
            String cronExpression = quartzJobAnnotation.cronExp();

            JobDetail jobDetail = jobScheduler.getJobDetail(new JobKey(jobName,jobGroup));
            if(null != jobDetail) {
                logger.error(">>>>>>>> tasks {} has multi impl [class: %s, %s], please check config.",jobName,jobClass);
                return RegisterResultEnum.ALREADY_EXISTED;
            }
            createAndRegisterJob(jobClass,jobName,jobGroup,cronExpression,null);
            return RegisterResultEnum.REGISTERED;
        } catch (Exception e) {
            logger.error(">>>>>>>> Failed to register tasks [{}],error is {}",jobClass, JSONUtils.toJSONString(e));
            return RegisterResultEnum.FAILED;
        }
    }
}
