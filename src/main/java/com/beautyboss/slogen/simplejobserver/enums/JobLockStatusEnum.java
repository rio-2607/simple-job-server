package com.beautyboss.slogen.simplejobserver.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
public enum JobLockStatusEnum {

    NEW(0,"新插入"),
    PROCESSING(1,"执行中");


    @Setter
    @Getter
    private int status;

    @Setter
    @Getter
    private String desc;

    private JobLockStatusEnum(int status,String desc) {
        this.status = status;
        this.desc = desc;
    }


}
