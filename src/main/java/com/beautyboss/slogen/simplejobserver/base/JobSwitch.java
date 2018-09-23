package com.beautyboss.slogen.simplejobserver.base;

import com.beautyboss.slogen.simplejobserver.register.SimpleQuartzJob;
import com.beautyboss.slogen.simplejobserver.data.SimpleJobSwitch;
import com.beautyboss.slogen.simplejobserver.mapper.SimpleJobSwitchMapper;
import com.beautyboss.slogen.simplejobserver.register.AnnoJobRegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
@Component("jobSwitch")
public class JobSwitch implements ApplicationListener{

    private static final Logger logger = LoggerFactory.getLogger(JobSwitch.class);

    @Resource
    private SimpleJobSwitchMapper amJobSwitchDao;

    public boolean isSwitchOff(String jobName) {
        if(StringUtils.isEmpty(jobName)) {
            return true;
        }
        SimpleJobSwitch simpleJobSwitch = amJobSwitchDao.getSwitch(jobName);
        return null == simpleJobSwitch || 0 == simpleJobSwitch.getStatus();
    }

    public void turnOn(String jobName) {
        amJobSwitchDao.turnOn(jobName);
    }

    public void turnOff(String jobName) {
        amJobSwitchDao.turnOff(jobName);
    }


    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof AnnoJobRegisterEvent) {
            addSwitch((ClusterJob) applicationEvent.getSource());
        }
    }

    private void addSwitch(ClusterJob bean) {
        Class<? extends ClusterJob> jobClass = bean.getClass();
        SimpleQuartzJob simpleQuartzJob = AnnotationUtils.findAnnotation(jobClass,SimpleQuartzJob.class);
        if(null == simpleQuartzJob) {
            return;
        }
        logger.info("Create switch for Job [{}].",bean.jobName());

        try {
            String jobName = simpleQuartzJob.name();

            SimpleJobSwitch aSwitch = amJobSwitchDao.getSwitch(jobName);
            if (null != aSwitch && jobName.equalsIgnoreCase(aSwitch.getJobName())) {
                logger.info("Job switch with [name: {}] already exits [status: {}].", jobName, aSwitch.getStatus());
                return;
            }

            SimpleJobSwitch hotelJobSwitch = new SimpleJobSwitch();
            hotelJobSwitch.setJobName(jobName);
            hotelJobSwitch.setStatus(0);
            hotelJobSwitch.setUpdateTime(new Date());
            amJobSwitchDao.create(hotelJobSwitch);

        } catch (Exception e) {

        }
    }
}
