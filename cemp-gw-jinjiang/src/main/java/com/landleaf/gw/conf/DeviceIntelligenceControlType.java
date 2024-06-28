package com.landleaf.gw.conf;

import lombok.Data;
import lombok.Getter;

/**
 * 设备智控类型
 */
public enum DeviceIntelligenceControlType {

    GUEST_ROOM_NORMAL(1, "客房正常开机", "{\"setTemp\":26, \"ktMode\":\"auto\", \"windMode\":\"auto\"}"),
    GUEST_ROOM_LOW(2, "客房夜晚", "{\"setTemp\":27,  \"ktMode\":\"auto\", \"windMode\":\"auto\"}"),
    LOBBY_MORNING(3, "大堂早上7.00~13.00", "{\"RST\":\"on\", \"setTemp\":25, \"ktMode\":\"auto\", \"windMode\":\"auto\"}"),
    LOBBY_NOON(4, "大堂中午13.00~17.00", "{\"RST\":\"on\", \"setTemp\":27, \"ktMode\":\"auto\", \"windMode\":\"auto\"}"),
    LOBBY_AFTERNOON(5, "大堂傍晚17.00~23.00", "{\"RST\":\"on\", \"setTemp\":25, \"ktMode\":\"auto\", \"windMode\":\"auto\"}"),
    LOBBY_OFF(6, "大堂关机", "{\"RST\":\"off\", \"setTemp\":25, \"ktMode\":\"auto\", \"windMode\":\"auto\"}"),
    AISLE_AFTERNOON(7, "过道傍晚17.00~21.00", "{\"RST\":\"on\", \"setTemp\":26, \"ktMode\":\"auto\",\"windMode\":\"auto\"}"),
    AISLE_OFF(8, "过道关机", "{\"RST\":\"off\", \"setTemp\":25, \"ktMode\":\"auto\", \"windMode\":\"auto\"}"),
    ;

    private DeviceIntelligenceControlType(int type, String desc, String cmdDetail) {
        this.type = type;
        this.desc = desc;
        this.cmdDetail = cmdDetail;
    }

    @Getter
    private int type;

    @Getter
    private String desc;

    @Getter
    private String cmdDetail;
}
