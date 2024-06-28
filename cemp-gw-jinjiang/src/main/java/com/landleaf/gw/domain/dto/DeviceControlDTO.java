package com.landleaf.gw.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备控制的DTO
 */
@Data
@Schema(name = "DeviceControlDTO对象", description = "设备控制的参数封装")
public class DeviceControlDTO {

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
     * 控制的详情
     */
    private List<DeviceControlDetailDTO> detail;

    /**
     * 当前运行状态，0表示关机，1表示运行
     */
    private int runningStatus;

    private Long ts;
}
