package com.hfi.insurance.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author ChenZX
 * @Date 2021/8/16 10:57
 * @Description:
 */
public final class DateUtil {

    private DateUtil() {
        super();
    }
    /**
     *
     * @Title  dateToStr
     * @Description
     * @param  date Date
     * @param  format String
     * @return String 字符串
     * @throws
     */
    public static String dateToStr(Date date, String format) {
        if (date == null) {
            return null;
        }
        DateFormat dateformat = new SimpleDateFormat(format);
        return dateformat.format(date);
    }

    /**
     *
     * @Title  getNowTimestampStr
     * @Description
     * @param
     * @return String 字符串
     * @throws
     */
    public static String getNowTimestampStr() {
        return dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
    }
}
