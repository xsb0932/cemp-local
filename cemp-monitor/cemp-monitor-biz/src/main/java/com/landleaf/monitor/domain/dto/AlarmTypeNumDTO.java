package com.landleaf.monitor.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AlarmTypeNumDTO {
    /**
     * 告警类型 数据字典（ALARM_TYPE）
     */
    @Schema(description = "告警类型 数据字典（ALARM_TYPE）")
    private String alarmType;
    /**
     * 告警类型 名称
     */
    @Schema(description = "确认的数量")
    private Integer confirmNum;
    /**
     * 告警数量
     */
    @Schema(description = "告警数量")
    private Integer number;
}
