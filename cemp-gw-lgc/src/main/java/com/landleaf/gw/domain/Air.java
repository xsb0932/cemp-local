package com.landleaf.gw.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 多参
 */
@Data
public class Air {

    /**
     * 设备编号
     */
    private String deviceId;

    /**
     * pm25
     */
    private BigDecimal pm25;

    /**
     * Temperature
     */
    private BigDecimal Temperature;

    /**
     * Humidity
     */
    private BigDecimal Humidity;

    /**
     * CO2
     */
    private BigDecimal CO2;

    /**
     * TVOC
     */
    private BigDecimal TVOC;

    /**
     * HCHO
     */
    private BigDecimal HCHO;


    public Map<String, Object> toMap() {
        Map<String, Object> valMap = Maps.newHashMap();
        if (null != pm25) {
            valMap.put("pm25", pm25);
        }
        if (null != Temperature) {
            valMap.put("Temperature", Temperature);
        }
        if (null != Humidity) {
            valMap.put("Humidity", Humidity);
        }
        if (null != CO2) {
            valMap.put("CO2", CO2);
        }
        if (null != TVOC) {
            valMap.put("TVOC", TVOC);
        }
        if (null != HCHO) {
            valMap.put("HCHO", HCHO);
        }
        return valMap;
    }
}
