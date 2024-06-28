package com.landleaf.monitor.domain.enums;

/**
 * 设备智控类型
 */
public enum DeviceIntelligenceControlType {

    GUEST_ROOM_NORMAL(1, "客房正常开机", "{}"),
    GUEST_ROOM_LOW(2, "客房夜晚", "{}"),
    LOBBY_MORNING(3, "大堂早上7.00~13.00", "{}"),
    LOBBY_NOON(4, "大堂中午13.00~17.00", "{}"),
    LOBBY_AFTERNOON(5, "大堂傍晚17.00~23.00", "{}"),
    LOBBY_OFF(6, "大堂关机", "{}"),
    AISLE_AFTERNOON(7, "过道傍晚17.00~21.00", "{}"),
    AISLE_OFF(8, "过道关机", "{}"),
    ;

    private DeviceIntelligenceControlType(int type, String desc, String cmdDetail) {
        this.type = type;
        this.desc = desc;
        this.cmdDetail = cmdDetail;
    }

    private int type;

    private String desc;

    private String cmdDetail;
}
