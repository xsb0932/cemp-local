package com.landleaf.comm.util;

import cn.hutool.core.util.StrUtil;
import com.landleaf.comm.util.object.SpringContextUtils;

/**
 * 替换天气图标访问地址
 *
 * @author Yang
 */
public class WeatherUtil {

    public static String replace(String origin) {
        String activeProfile = SpringContextUtils.getActiveProfile();
        if (!StrUtil.equals("prod", activeProfile)) {
            return origin;
        }
        return StrUtil.replace(origin, "http://47.100.3.98:30002", "https://emp.landib.com/weather");
    }
}
