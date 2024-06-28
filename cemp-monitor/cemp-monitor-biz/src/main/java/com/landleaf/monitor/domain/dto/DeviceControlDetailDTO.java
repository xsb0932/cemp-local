package com.landleaf.monitor.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 控制详情
 */
@Data
@Schema(name = "DeviceControlDetailDTO对象", description = "设备控制的详情参数封装")
public class DeviceControlDetailDTO {
    /**
     * 属性编码
     */
    private String attrCode;

    /**
     * 属性值
     */
    private String value;
}
