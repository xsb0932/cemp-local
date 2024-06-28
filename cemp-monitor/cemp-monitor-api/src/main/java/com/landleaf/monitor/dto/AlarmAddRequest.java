package com.landleaf.monitor.dto;

import lombok.Data;

@Data
public class AlarmAddRequest {
    /**
     * 租户编号
     */
    private Long tenantId;


    /**
     * 项目编号
     */
    private String bizProjId;

    private ProductAlarmConf alarmInfo;

    /**
     * @see com.landleaf.monitor.enums.AlarmObjTypeEnum
     */
    private String alarmObjType;

    /**
     * 如果alarm为设备告警，此为bizDeviceid，别的，按自己的类型来
     */
    private String objId;

    /**
     * @see com.landleaf.monitor.enums.AlarmTypeEnum
     */
    private String alarmType;

    /**
     * @see com.landleaf.monitor.enums.AlarmStatusEnum
     */
    private String alarmStatus;

    /**
     * 告警发生时间
     */
    private Long time;

    /**
     * 如果是存在current，修改his时，包含此信息，否则为null
     */
    private String alarmBizId;

    /**
     * 如果是存在current，修改his时，包含此信息，否则为null
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;
}
