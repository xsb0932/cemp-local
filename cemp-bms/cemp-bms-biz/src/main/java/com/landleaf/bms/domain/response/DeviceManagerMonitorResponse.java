package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设备管理-运行监控对象")
public class DeviceManagerMonitorResponse {
    @Schema(description = "设备控制服务")
    private List<DeviceManagerMonitorService> services;
    @Schema(description = "属性")
    private List<DeviceManagerMonitorAttribute> attributes;
    @Schema(description = "属性更新时间")
    private String attrUploadTime;
    @Schema(description = "参数")
    private List<DeviceManagerMonitorProperty> properties;
}
