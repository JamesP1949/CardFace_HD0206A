package com.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JamesP949 on 2017/3/8.
 * Function:时间处理
 */

public class TimeUtils {

    public static String getDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =  new Date(time);
        return simpleDateFormat.format(date);
    }
}
