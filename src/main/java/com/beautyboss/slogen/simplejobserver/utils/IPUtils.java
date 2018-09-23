package com.beautyboss.slogen.simplejobserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Author : Slogen
 * Date   : 2017/12/24
 */
public class IPUtils {

    private static Logger logger = LoggerFactory.getLogger(IPUtils.class);

    public static String getHostIP() {
        String host = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            host = addr.getHostName();
        } catch (UnknownHostException e) {
            logger.error( "Failed to get ip of host,The exception is {}", e.getCause());
        }
        return host;
    }
}
