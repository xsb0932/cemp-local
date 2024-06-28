package com.landleaf.monitor.domain.vo;

import com.landleaf.monitor.domain.response.NodeProjectDeviceTreeResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "设备信息封装")
public class DeviceTreeDisplayVO extends NodeProjectDeviceTreeResponse {
    /**
     * 设备id（全局唯一id）
     */
    @Schema(description = "设备id（全局唯一id）")
    private String bizDeviceId;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String name;

    /**
     * 产品id（全局唯一id）
     */
    @Schema(description = "产品id（全局唯一id）")
    private String bizProductId;

    /**
     * 品类id（全局唯一id）
     */
    @Schema(description = "品类id（全局唯一id）")
    private String bizCategoryId;

    /**
     * 设备编码（校验唯一）
     */
    @Schema(description = "设备编码（校验唯一）")
    private String code;

    /**
     * 是否可点击，非设备，均不可选
     */
    @Schema(name = "是否可点击，非设备，均不可选")
    private Boolean checkable = true;

    /**
     * 设备属性
     */
    @Schema(description = "设备属性")
    private List<DeviceAttrDisplayVO> attrCodes;

    public String getId() {
        return bizDeviceId;
    }
}
