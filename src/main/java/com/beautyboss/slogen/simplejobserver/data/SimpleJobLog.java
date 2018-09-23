package com.beautyboss.slogen.simplejobserver.data;

import lombok.Data;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
@Data
public class SimpleJobLog {

    private int id;

    private String jobName;

    private String startedTime;

    private String finishedTime;

    private String host;

    private String message;
}
