package com.beautyboss.slogen.simplejobserver.web.status;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
public enum StatusCode implements Status {
    SERVICE_RUN_SUCCESS(10000, "服务运行成功"),
    JOB_FORBIDDEN(10001,"%s"),

    SERVICE_RUN_ERROR(99999, "服务器忙,请稍后再试,原因:%s");

    private int status;
    private String msg;

    StatusCode(int status, String message) {
        this.status = status;
        this.msg = message;
    }

    public boolean isSuccess() {
        return getStatus() > 0;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
