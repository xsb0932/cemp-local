package com.landleaf.bms.domain.request;

import com.landleaf.bms.api.json.ValueDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * 设备参数明细表实体类
 *
 * @author hebin
 * @since 2023-07-24
 */
@Data
@Schema(name = "DeviceParameterDetailRequest对象", description = "设备参数明细表")
public class DeviceParameterDetailRequest  {

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * ID
     */
    @Schema(description = "productParameterId")
    private Long productParameterId;

    /**
     * 产品ID
     */
    @Schema(description = "产品ID")
    private Long productId;

    /**
     * 功能标识符
     */
    @Schema(description = "功能标识符")
    private String identifier;

    /**
     * 功能类型
     * 系统默认功能、系统可选功能、标准可选功能
     */
    @Schema(description = "功能类型* 系统默认功能、系统可选功能、标准可选功能")
    private String functionName;

    /**
     * 参数值
     */
    @Schema(description = "参数值")
    private String value;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String bizDeviceId;

    /**
     * 数据类型
     */
    @Schema(description = "数据类型")
    private String dataTpe;

    /**
     * 值描述
     */
    @Schema(description = "值描述")
    private List<ValueDescription> valueDescription;
}
