package com.landleaf.monitor.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备当前属性值对象
 *
 * @author Yang
 */
@Data
public class DeviceCurrentVO {
    /**
     * 设备id
     */
    private String bizDeviceId;
    /**
     * 品类id
     */
    private String bizCategoryId;
    /**
     * 产品id
     */
    private String bizProductId;
    /**
     * 项目id
     */
    private String bizProjectId;
    /**
     * 属性
     */
    private List<DeviceCurrentAttrVO> attrs;
}
