package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.DeviceParameterEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备参数变更请求参数
 *
 * @author yue lin
 * @since 2023/6/25 15:43
 */
@Data
public class DeviceParameterChangeRequest {

    /**
     * 设备参数新增请求参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "设备参数新增请求参数")
    public static class Create extends ValueDescriptionParam {

        /**
         * 功能标识符
         */
        @NotBlank(message = "功能标识符不能为空")
        @Schema(description = "功能标识符")
        private String identifier;

        /**
         * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-设备参数
         */
        @NotBlank(message = "功能标识符不能为空")
        @Schema(description = "功能类别", example = "设备参数")
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

        public DeviceParameterEntity toEntity() {
            validate();
            DeviceParameterEntity deviceParameter = new DeviceParameterEntity();
            deviceParameter.setIdentifier(this.identifier);
            deviceParameter.setFunctionCategory(this.functionCategory);
            deviceParameter.setFunctionName(this.functionName);
            deviceParameter.setFunctionType(this.functionType);
            deviceParameter.setDataType(getDataType());
            deviceParameter.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
            deviceParameter.setRw(this.rw);
            return deviceParameter;
        }
    }

    /**
     * 设备参数更新请求参数（数据类型只用来做校验值描述）
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "设备参数更新请求参数（数据类型只用来做校验值描述）")
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

        public void mergeEntity(DeviceParameterEntity entity) {
            validate();
            entity.setFunctionName(this.functionName);
            entity.setDataType(getDataType());
            entity.setRw(this.rw);
            entity.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
        }

    }

}
