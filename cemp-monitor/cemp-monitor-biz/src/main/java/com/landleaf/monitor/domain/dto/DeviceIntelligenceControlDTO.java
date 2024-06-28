package com.landleaf.monitor.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备智能控制的DTO
 */
@Data
@Schema(name = "DeviceIntelligenceControlDTO对象", description = "设备智能控制的参数封装")
@NoArgsConstructor
@AllArgsConstructor
public class DeviceIntelligenceControlDTO {

    /**
     * 与执行段通讯时的msgId,与页面交互时不需要
     */
    @Schema(hidden = true)
    private String msgId;

    /**
     * 设备业务编号
     */
    private String bizDeviceId;

    /**
     * 指控的类型
     */
    private int type;



    /**
     * 当前运行状态 0表示关机，1表示运行
     */
    private int runningStatus;

    private long ts;
}
