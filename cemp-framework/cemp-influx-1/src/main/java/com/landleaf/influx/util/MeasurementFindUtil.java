package com.landleaf.influx.util;

/**
 * 根据各种条件，获取measure的类
 */
public final class MeasurementFindUtil {
    private static String DEVICE_STATUS_PREFIX = "device_status_";
    public static String WEATHER_HISTORY = "weather_history";

    /**
     * 根据prodcode,获取对应的设备状态存储的measurement
     *
     * @param prodCode
     * @return
     */
    public static String getDeviceStatusMeasurementByProdCode(String prodCode) {
        return DEVICE_STATUS_PREFIX + prodCode;
    }
}
