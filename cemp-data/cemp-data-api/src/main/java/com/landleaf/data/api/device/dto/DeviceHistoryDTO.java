package com.landleaf.data.api.device.dto;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Data
public class DeviceHistoryDTO {
    /**
     * 设备业务id
     */
    private String bizDeviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 属性编码
     */
    private String attrCode;

    /**
     * 属性名
     */
    private String attrName;

    /**
     * 时间
     */
    private List<String> times;

    /**
     * 属性值
     */
    private List<String> values;

    public Map<String, String> getValueMap() {
        if (!CollectionUtils.isEmpty(times) && !CollectionUtils.isEmpty(values)) {
            Map<String, String> valueMap = Maps.newHashMap();
            for (int i = 0; i < times.size(); i++) {
                if (values.size() < i) {
                    break;
                }
                valueMap.put(times.get(i), values.get(i));
            }
            return valueMap;
        }
        return Maps.newHashMap();
    }
}
