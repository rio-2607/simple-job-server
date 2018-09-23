package com.beautyboss.slogen.simplejobserver.mapper;

import com.beautyboss.slogen.simplejobserver.data.SimpleJobSwitch;
import org.apache.ibatis.annotations.Param;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
public interface SimpleJobSwitchMapper {

    void create(@Param("simpleJobSwitch") SimpleJobSwitch simpleJobSwitch);

    void turnOn(@Param("jobName") String jobName);

    void turnOff(@Param("jobName") String jobName);

    SimpleJobSwitch getSwitch(@Param("jobName") String jobName);
}
