package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备IOT返回值
 *
 * @author Tycoon
 * @since 2023/8/18 15:59
 **/
@Data
public class DeviceIoResponse {

    /**
     * 设备业务ID
     */
    @Schema(description = "设备业务ID")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 项目业务ID
     */
    @Schema(description = "项目业务ID")
    private String bizProjectId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

}
