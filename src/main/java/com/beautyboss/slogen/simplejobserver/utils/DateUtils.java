package com.beautyboss.slogen.simplejobserver.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author : Slogen
 * Date   : 2017/12/17
 */
public class DateUtils {
    public static String format(Date date,String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return simpleDateFormat.format(date);
    }
}
