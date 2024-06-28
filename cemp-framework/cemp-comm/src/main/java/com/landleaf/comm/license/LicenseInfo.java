package com.landleaf.comm.license;

import lombok.Data;

@Data
public class LicenseInfo {
    /**
     * 是否合法
     */
    private boolean legal;

    private String mac;

    private String endTime;

    /**
     * 用户限制数量
     */
    private int userLimit;

    /**
     * 设备限制数量
     */
    private int deviceLimit;

    /**
     * 项目限制数量
     */
    private int projLimit;
}
