package com.beautyboss.slogen.simplejobserver.data;

import lombok.Data;

import java.util.Date;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
@Data
public class SimpleJobSwitch {

    private int id;

    private String jobName;

    private int status;

    private Date addTime;

    private Date updateTime;

    @Override
    public String toString() {
        return "";
    }
}
