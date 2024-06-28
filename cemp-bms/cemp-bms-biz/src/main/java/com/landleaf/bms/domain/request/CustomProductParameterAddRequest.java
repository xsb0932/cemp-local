package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.ProductProductParameterEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品-新增自定义产品参数请求参数
 *
 * @author 张力方
 * @since 2023/7/4
 **/
@Data
@Schema(name = "产品-新增自定义产品参数请求参数", description = "产品-新增自定义产品参数请求参数")
public class CustomProductParameterAddRequest extends ValueDescriptionParam {
    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "wxxxx001")
    private Long productId;

    /**
     * 功能标识符
     * <p>
     * 1、功能管理中唯一
     * 2、英文开头，可包含英文何数字的字符串
     */
    @NotBlank(message = "功能标识符不能为空")
    @Schema(description = "功能标识符，英文开头，可包含英文何数字的字符串", requiredMode = Schema.RequiredMode.REQUIRED, example = "wxxxx001")
    private String identifier;

    /**
     * 功能名称
     */
    @NotBlank(message = "功能名称不能为空")
    @Schema(description = "功能名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    private String functionName;

    /**
     * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-产品参数
     */
    @NotBlank(message = "功能类别不能为空")
    @Schema(description = "功能类别（字典编码-PRODUCT_FUNCTION_CATEGORY）-产品参数", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    private String functionCategory;

    /**
     * 功能类型（字典编码-PRODUCT_FUNCTION_TYPE）
     * <p>
     * 系统默认功能、系统可选功能、标准可选功能
     */
    @NotBlank(message = "功能类型不能为空")
    @Schema(description = "功能类型（字典编码-PRODUCT_FUNCTION_TYPE）", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    private String functionType;

    public ProductProductParameterEntity toEntity() {
        validate();
        ProductProductParameterEntity productProductParameterEntity = new ProductProductParameterEntity();
        productProductParameterEntity.setProductId(this.productId);
        productProductParameterEntity.setIdentifier(this.identifier);
        productProductParameterEntity.setFunctionName(this.functionName);
        productProductParameterEntity.setFunctionCategory(this.functionCategory);
        productProductParameterEntity.setFunctionType(this.functionType);
        productProductParameterEntity.setDataType(getDataType());
        productProductParameterEntity.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
        return productProductParameterEntity;

    }
}
