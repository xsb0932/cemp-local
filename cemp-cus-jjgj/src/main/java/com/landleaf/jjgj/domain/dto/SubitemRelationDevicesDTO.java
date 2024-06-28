package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分类计算对应的设备的dto
 */
@Data
@Schema(name = "SubitemRelationDevicesDTO", description = "分类计算对应的设备的dto")
public class SubitemRelationDevicesDTO {
    /**
     * bizDeviceId
     */
    @Schema(description = "bizDeviceId")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 计算标志位
     */
    @Schema(description = "计算标志位")
    private String computerTag;

    /**
     * 计算标志位描述
     */
    @Schema(description = "计算标志位描述")
    private String computerTagDesc;
}
