package com.landleaf.energy.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设备充电返回值
 *
 * @author yue lin
 * @since 2023/8/3 10:08
 */
@Data
@Schema(description = "设备充电返回值")
public class DeviceElectricityResponse {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 电量
     */
    @Schema(description = "电量")
    private BigDecimal epimportTotal;

}
