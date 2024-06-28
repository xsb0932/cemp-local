package com.landleaf.monitor.domain.vo;

import lombok.Data;

@Data
public class DeviceCurrentAttrVO {
    /**
     * 属性code
     */
    private String code;
    /**
     * 属性值
     */
    private Object value;
    //TODO 类型or单位是否要传未知
}
