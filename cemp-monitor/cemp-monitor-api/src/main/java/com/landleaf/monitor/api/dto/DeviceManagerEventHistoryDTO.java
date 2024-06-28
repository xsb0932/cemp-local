package com.landleaf.monitor.api.dto;

import cn.hutool.core.date.LocalDateTimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "设备管理-历史事件")
public class DeviceManagerEventHistoryDTO {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "事件发生时间")
    private LocalDateTime eventTime;

    @Schema(description = "事件发生时间（格式化）")
    private String eventTimeStr;

    @Schema(description = "事件id")
    private String eventId;

    @Schema(description = "告警类型 数据字典（ALARM_TYPE）")
    private String alarmType;

    @Schema(description = "告警类型名称")
    private String alarmTypeName;

    @Schema(description = "告警码")
    private String alarmCode;

    @Schema(description = "告警描述")
    private String alarmDesc;

    @Schema(description = "告警等级 数据字典（ALARM_LEVEL）")
    private String alarmLevel;

    @Schema(description = "告警等级名称")
    private String alarmLevelName;

    @Schema(description = "告警状态 数据字典（ALARM_STATUS）")
    private String alarmStatus;

    @Schema(description = "告警状态 名称")
    private String alarmStatusName;

    @Schema(description = "确认状态 是否确认")
    private Boolean isConfirm;

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
        this.eventTimeStr = LocalDateTimeUtil.format(eventTime, "yyyy-MM-dd HH:mm:ss");
    }

}
