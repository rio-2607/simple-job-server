package com.beautyboss.slogen.simplejobserver.lock;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
public interface Lock {

    boolean lock(String jobName);

    void unlock(String jobName);

    boolean isLocked(String jobName);
}
