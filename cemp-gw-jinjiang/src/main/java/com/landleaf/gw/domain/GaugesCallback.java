package com.landleaf.gw.domain;

import lombok.Data;


/**
 * 计量表的回调
 */
@Data
public class GaugesCallback {
    /**
     * 设备 ID
     */
    private String devId;

    /**
     * 设备名
     */
    private String devName;

    /**
     * （设备备注名称）
     */
    private String name;

    /**
     * 设备电压
     */
    private String devVo;

    /**
     * 采集时间
     */
    private String createTime;

    /**
     * 是否是指针表
     */
    private String isPointerMeter;

    /**
     * 是否是多区域识别表
     */
    private String isMultiRegionMeter;

    /**
     * 解析结果
     */
    private String result;
}
