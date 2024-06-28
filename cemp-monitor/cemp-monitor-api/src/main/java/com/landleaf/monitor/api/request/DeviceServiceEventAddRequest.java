package com.landleaf.monitor.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceServiceEventAddRequest {
    /**
     * 告警发生时间
     */
    @NotNull(message = "事件时间不能为空")
    private Long time;

    /**
     * 租户编号
     */
    @NotNull(message = "租户id不能为空")
    private Long tenantId;

    /**
     * 项目编号
     */
    @NotBlank(message = "项目id不能为空")
    private String bizProjectId;

    /**
     * 设备id
     */
    @NotBlank(message = "设备id不能为空")
    private String bizDeviceId;

    /**
     * 告警CODE
     */
    @NotBlank(message = "serviceId不能为空")
    private String serviceId;
    /**
     * 告警描述
     */
    @NotBlank(message = "服务指令描述不能为空")
    private String serviceDesc;

}
