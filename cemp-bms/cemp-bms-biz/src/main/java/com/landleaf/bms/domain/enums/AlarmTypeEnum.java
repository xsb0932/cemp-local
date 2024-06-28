package com.landleaf.bms.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AlarmTypeEnum
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Getter
@AllArgsConstructor
public enum AlarmTypeEnum {
    /*
     * 设备告警
     */
    DEVICE_ALARM("devAlarm", "设备告警"),
    /**
     * 规则告警
     */
    RULE_ALARM("ruleAlarm", "规则告警"),
    /**
     * 通讯告警
     */
    CON_ALARM("conAlarm", "通讯告警"),
    /**
     * 其他（普通事件）
     */
    OTHER("other", "其他（普通事件）");

    private final String code;
    private final String name;

    public static AlarmTypeEnum fromName(String name) {
        for (AlarmTypeEnum value : AlarmTypeEnum.values()) {
            if (name.equals(value.getName())) {
                return value;
            }
        }
        return null;
    }

    public static String getName(String code) {
        for (AlarmTypeEnum value : AlarmTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value.getName();
            }
        }
        return null;
    }
}
