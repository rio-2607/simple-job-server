package com.beautyboss.slogen.simplejobserver.web.vo;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
@Data
public class JobDetailVO implements Comparable{

    private String name;
    private String group = "DEFAULT";
    private String description = "";
    private String jobClass;
    private String status = "WAITING";
    private boolean switchOff = true;
    private long runTime;
    private Date fireTime;
    private List<JobTriggerVO> triggerList = new ArrayList<JobTriggerVO>();

    public void setJobClass(Class jobClass) {
        this.jobClass = jobClass.getName();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    public int compareTo(Object o) {
        if (this.switchOff == ((JobDetailVO) o).switchOff) {
            if (this.status.equalsIgnoreCase(((JobDetailVO) o).status)) {
                return this.name.toLowerCase().compareTo(((JobDetailVO) o).name.toLowerCase());
            } else {
                if (this.status.equalsIgnoreCase("WAITING")) return 1;
                else return -1;
            }
        } else {
            if (this.switchOff) return 1;
            else return -1;
        }
    }

    @Override
    public boolean equals(Object obj){
        return super.equals(obj);
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }
}
