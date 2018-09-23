package com.beautyboss.slogen.simplejobserver.enums;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
public enum TaskTypeEnum {

    SINGLE("single","分布式任务"),
    ALL("all","并行的任务");

    private String type;

    private String desc;

    TaskTypeEnum(String type,String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return this.type;
    }
}
