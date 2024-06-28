package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.ProductDeviceAttributeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品-新增自定义设备属性请求参数
 *
 * @author 张力方
 * @since 2023/7/4
 **/
@Data
@Schema(name = "产品-新增自定义设备属性请求参数", description = "产品-新增自定义设备属性请求参数")
public class CustomDeviceAttributeAddRequest extends ValueDescriptionParam {
    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "wxxxx001")
    private Long productId;

    /**
     * 功能标识符
     */
    @NotBlank(message = "功能标识符不能为空")
    @Schema(description = "功能标识符")
    private String identifier;

    /**
     * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-设备属性
     */
    @NotBlank(message = "功能标识符不能为空")
    @Schema(description = "功能类别", example = "设备属性")
    private String functionCategory;

    /**
     * 功能名称
     */
    @NotBlank(message = "功能名称不能为空")
    @Schema(description = "功能名称")
    private String functionName;

    /**
     * 功能类型（字典编码-PRODUCT_FUNCTION_TYPE）
     * <p>
     * 系统默认功能、系统可选功能、标准可选功能
     */
    @NotBlank(message = "功能类型不能为空")
    @Schema(description = "功能类型（字典编码-PRODUCT_FUNCTION_TYPE）")
    private String functionType;

    /**
     * 是否可读写（字典编码-RW_TYPE）
     */
    @NotBlank(message = "读写不能为空")
    @Schema(description = "是否可读写（字典编码-RW_TYPE）")
    private String rw;

    public ProductDeviceAttributeEntity toEntity() {
        validate();
        ProductDeviceAttributeEntity deviceAttribute = new ProductDeviceAttributeEntity();
        deviceAttribute.setProductId(this.productId);
        deviceAttribute.setIdentifier(this.identifier);
        deviceAttribute.setFunctionCategory(this.functionCategory);
        deviceAttribute.setFunctionName(this.functionName);
        deviceAttribute.setFunctionType(this.functionType);
        deviceAttribute.setRw(this.rw);
        deviceAttribute.setDataType(getDataType());
        deviceAttribute.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
        return deviceAttribute;
    }
}
