package com.landleaf.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 告警响应对象
 *
 * @author 张力方
 * @since 2023/8/22
 **/
@Data
public class AlarmResponse {
    /**
     * 时间yyyy/MM/dd
     */
    @Schema(description = "时间yyyy/MM/dd")
    private String time;
    /**
     * 告警设备
     */
    @Schema(description = "告警设备")
    private String alarmDevice;
    /**
     * 告警描述
     */
    @Schema(description = "告警描述")
    private String alarmDesc;
    /**
     * 告警状态
     */
    @Schema(description = "告警状态")
    private String alarmStatus;

    /**
     * 预警类型
     */
    @Schema(description = "预警类型")
    private String alarmType;


}
