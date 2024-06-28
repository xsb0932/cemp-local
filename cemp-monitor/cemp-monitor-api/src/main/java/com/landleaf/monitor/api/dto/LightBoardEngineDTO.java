package com.landleaf.monitor.api.dto;

import lombok.Data;

@Data
public class LightBoardEngineDTO {
    private String bizProjectId;
    private String bizDeviceId;
    private String deviceName;
    private String code;

    private Boolean hasAlarm;
    private Boolean hasUCAlarm;

    private String zfBizDeviceId;
    private String zfName;
    private String zfCode;
    private String evaporatingInTempUnit;
    private String evaporatingOutTempUnit;

    private String lnBizDeviceId;
    private String lnName;
    private String lnCode;
    private String condensingInTempUnit;
    private String condensingOutTempUnit;

}
