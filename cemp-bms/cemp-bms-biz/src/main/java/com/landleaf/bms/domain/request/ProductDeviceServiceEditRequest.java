package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.ProductDeviceServiceEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品-编辑设备服务请求参数
 *
 * @author 张力方
 * @since 2023/7/4
 **/
@Data
@Schema(name = "产品-编辑设备服务请求参数", description = "产品-编辑设备服务请求参数")
public class ProductDeviceServiceEditRequest extends FunctionParameterParam {
    /**
     * 设备服务id
     */
    @NotNull(message = "设备服务id不能为空")
    @Schema(description = "设备服务id不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 功能名称
     */
    @NotBlank(message = "功能名称不能为空")
    @Schema(description = "功能名称")
    private String functionName;

    public void mergeEntity(ProductDeviceServiceEntity entity) {
        validate();
        entity.setFunctionName(this.functionName);
        entity.setServiceParameter(getFunctionParameter().stream().map(FunctionParam::toFunctionParameter).toList());
        entity.setResponseParameter(getResponseParameter().stream().map(FunctionParam::toFunctionParameter).toList());
    }

}
