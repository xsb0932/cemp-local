package com.landleaf.gw.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 气表的信息封装
 */
@Data
public class GasMeter {

    /**
     * 设备编号
     */
    private String deviceId;
    /**
     * 气总用量
     */
    private BigDecimal Gascons;

    public Map<String, Object> toMap() {
        Map<String, Object> valMap = Maps.newHashMap();
        if (null != Gascons) {
            valMap.put("Gascons", Gascons);
        }
        return valMap;
    }
}
