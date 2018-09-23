package com.beautyboss.slogen.simplejobserver.web.status;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
public interface Status {

    /**
     * 获取状态值
     * @return
     */
    int getStatus();

    /**
     * 获取状态码
     * @return
     */
    String getCode();

    /**
     * 获取状态信息
     * @return
     */
    String getMsg();
}
