package com.landleaf.monitor.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 历史事件
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Data
@TableName(value = "tb_history_event")
public class HistoryEventEntity extends TenantBaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
