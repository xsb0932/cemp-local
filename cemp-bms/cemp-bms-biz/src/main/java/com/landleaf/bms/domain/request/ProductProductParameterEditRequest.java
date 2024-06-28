package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.ProductProductParameterEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品-编辑产品参数请求参数
 *
 * @author 张力方
 * @since 2023/7/4
 **/
@Data
@Schema(name = "产品-编辑产品参数请求参数", description = "产品-编辑产品参数请求参数")
public class ProductProductParameterEditRequest extends ValueDescriptionParam {

    /**
     * 产品参数id
     */
    @NotNull(message = "产品参数id不能为空")
    @Schema(description = "产品参数id不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 功能名称
     */
    @NotBlank(message = "功能名称不能为空")
    @Schema(description = "功能名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    private String functionName;

    public void mergeEntity(ProductProductParameterEntity entity){
        validate();
        entity.setFunctionName(this.functionName);
        entity.setDataType(getDataType());
        entity.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
    }

}
