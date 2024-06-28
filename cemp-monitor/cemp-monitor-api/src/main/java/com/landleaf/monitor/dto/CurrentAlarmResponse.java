package com.landleaf.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前告警
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Data
public class CurrentAlarmResponse {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 事件id
     */
    private String eventId;
    /**
     * 事件类型 数据字典（EVENT_TYPE）
     */
    private String eventType;
    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;
    /**
     * 告警对象类型 数据字典（ALARM_OBJ_TYPE）
     */
    private String alarmObjType;
    /**
     * 告警对象id
     */
    private String objId;
    /**
     * 所属项目id
     */
    private String projectBizId;
    /**
     * 告警业务id
     */
    private String alarmBizId;
    /**
     * 告警码
     */
    private String alarmCode;
    /**
     * 告警类型 数据字典（ALARM_TYPE）
     */
    private String alarmType;
    /**
     * 告警等级 数据字典（ALARM_LEVEL）
     */
    private String alarmLevel;
    /**
     * 告警描述
     */
    private String alarmDesc;
    /**
     * 告警状态 数据字典（ALARM_STATUS）
     */
    private String alarmStatus;
}
