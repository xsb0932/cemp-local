package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.DeviceAttributeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备属性变更请求参数
 *
 * @author yue lin
 * @since 2023/6/25 15:43
 */
@Data
public class DeviceAttributeChangeRequest {

    /**
     * 设备属性新增请求参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "设备属性新增请求参数")
    public static class Create extends ValueDescriptionParam {

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
         * 读写(RW_TYPE)
         */
        @NotBlank(message = "读写不能为空")
        @Schema(description = "读写(RW_TYPE)")
        private String rw;

        public DeviceAttributeEntity toEntity() {
            validate();
            DeviceAttributeEntity deviceAttribute = new DeviceAttributeEntity();
            deviceAttribute.setIdentifier(this.identifier);
            deviceAttribute.setFunctionCategory(this.functionCategory);
            deviceAttribute.setFunctionName(this.functionName);
            deviceAttribute.setFunctionType(this.functionType);
            deviceAttribute.setDataType(getDataType());
            deviceAttribute.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
            deviceAttribute.setRw(this.rw);
            return deviceAttribute;
        }
    }

    /**
     * 设备属性更新请求参数（数据类型只用来做校验值描述）
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "设备属性更新请求参数（数据类型只用来做校验值描述）")
    public static class Update extends ValueDescriptionParam {

        /**
         * id
         */
        @NotNull(message = "id不能为空")
        @Schema(description = "id（新增必须为空，更新必须不为空）")
        private Long id;

        /**
         * 功能名称
         */
        @NotBlank(message = "功能名称不能为空")
        @Schema(description = "功能名称")
        private String functionName;

        /**
         * 读写(RW_TYPE)
         */
        @NotBlank(message = "读写不能为空")
        @Schema(description = "读写(RW_TYPE)")
        private String rw;

        public void mergeEntity(DeviceAttributeEntity entity) {
            validate();
            entity.setFunctionName(this.functionName);
            entity.setDataType(getDataType());
            entity.setRw(this.rw);
            entity.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
        }

    }

}
