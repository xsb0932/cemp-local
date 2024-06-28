package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.ProductDeviceParameterEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 产品-编辑设备参数请求参数
 *
 * @author 张力方
 * @since 2023/7/4
 **/
@Data
@Schema(name = "产品-编辑设备参数请求参数", description = "产品-编辑设备参数请求参数")
public class ProductDeviceParameterEditRequest extends ValueDescriptionParam {
    /**
     * 设备参数id
     */
    @NotNull(message = "设备参数id不能为空")
    @Schema(description = "设备参数id不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 功能名称
     */
    @NotBlank(message = "功能名称不能为空")
    @Schema(description = "功能名称")
    private String functionName;

    /**
     * 是否可读写（字典编码-RW_TYPE）
     */
    @NotBlank(message = "读写不能为空")
    @Schema(description = "是否可读写（字典编码-RW_TYPE）")
    private String rw;

    public void mergeEntity(ProductDeviceParameterEntity entity) {
        validate();
        entity.setFunctionName(this.functionName);
        entity.setDataType(getDataType());
        entity.setRw(this.rw);
        entity.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
    }

}
