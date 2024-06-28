package com.landleaf.gw.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 水表的信息封装
 */
@Data
public class WaterMeter {

    /**
     * 设备编号
     */
    private String deviceId;
    /**
     * 水总用量
     */
    private BigDecimal Watercons;

    public Map<String, Object> toMap() {
        Map<String, Object> valMap = Maps.newHashMap();
        if (null != Watercons) {
            valMap.put("Watercons", Watercons);
        }
        return valMap;
    }
}
