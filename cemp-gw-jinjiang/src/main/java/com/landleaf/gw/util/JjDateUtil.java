package com.landleaf.gw.util;

import cn.hutool.core.date.DateUtil;

public final class JjDateUtil {
    /**
     * 将支付穿格式为yyyy-MM-dd HH:mm:ss的字符串，转为对应的unix时间戳，
     *
     * @param timeStr 时间类字符串，格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long parseTimeStr2Long(String timeStr) {
        return DateUtil.parseDateTime(timeStr).getTime() / 1000;
    }
}
