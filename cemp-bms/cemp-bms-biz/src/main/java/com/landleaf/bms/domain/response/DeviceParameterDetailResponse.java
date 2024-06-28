package com.landleaf.bms.domain.response;

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
public class DeviceParameterDetailResponse {

    public DeviceParameterDetailResponse(final Long id, final Long productParameterId, final String identifier, final String functionName) {
        this.id = id;
        this.productParameterId = productParameterId;
        this.identifier = identifier;
        this.functionName = functionName;
    }

    public DeviceParameterDetailResponse(final Long id, final Long productParameterId, final String identifier, final String functionName,String value) {
        this.id = id;
        this.productParameterId = productParameterId;
        this.identifier = identifier;
        this.functionName = functionName;
        this.value = value;
    }

    public DeviceParameterDetailResponse() {
    }

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
