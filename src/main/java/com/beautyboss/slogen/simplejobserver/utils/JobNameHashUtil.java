package com.beautyboss.slogen.simplejobserver.utils;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
public class JobNameHashUtil {

    public static int SDBMHash16(String jobName) {
        int i = jobName.hashCode();
        return (0xFFFF & i) ^ (i >>> 16);
    }
}
