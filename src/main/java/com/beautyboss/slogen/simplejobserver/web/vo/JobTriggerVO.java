package com.beautyboss.slogen.simplejobserver.web.vo;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
@Data
public class JobTriggerVO {

    private String name;
    private String group;
    private int priority;
    private String description = "";
    private String cronExpression;
    private Date startTime;
    private Date endTime;
    private Date nextFireTime;
    private Date previousFireTime;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
