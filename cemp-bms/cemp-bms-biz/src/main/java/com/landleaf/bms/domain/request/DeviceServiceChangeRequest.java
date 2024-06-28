package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.DeviceServiceEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备服务变更
 *
 * @author yue lin
 * @since 2023/6/27 15:24
 */
@Data
@Schema(description = "设备服务变更")
public class DeviceServiceChangeRequest extends FunctionParameterParam {


    /**
     * 设备服务新增请求参数
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "设备服务新增请求参数")
    public static class Create extends FunctionParameterParam {

        /**
         * 功能标识符
         */
        @NotBlank(message = "功能标识符不能为空")
        @Schema(description = "功能标识符")
        private String identifier;

        /**
         * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-设备服务
         */
        @NotBlank(message = "功能标识符不能为空")
        @Schema(description = "功能类别", example = "设备服务")
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

        public DeviceServiceEntity toEntity() {
            validate();
            DeviceServiceEntity deviceService = new DeviceServiceEntity();
            deviceService.setIdentifier(this.identifier);
            deviceService.setFunctionCategory(this.functionCategory);
            deviceService.setFunctionName(this.functionName);
            deviceService.setFunctionType(this.functionType);
            deviceService.setServiceParameter(getFunctionParameter().stream().map(FunctionParam::toFunctionParameter).toList());
            deviceService.setResponseParameter(getResponseParameter().stream().map(FunctionParam::toFunctionParameter).toList());
            return deviceService;
        }
    }

    /**
     * 设备服务更新请求参数（数据类型只用来做校验值描述）
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "设备服务更新请求参数（数据类型只用来做校验值描述）")
    public static class Update extends FunctionParameterParam {

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

        public void mergeEntity(DeviceServiceEntity entity) {
            validate();
            entity.setFunctionName(this.functionName);
            entity.setServiceParameter(getFunctionParameter().stream().map(FunctionParam::toFunctionParameter).toList());
            entity.setResponseParameter(getResponseParameter().stream().map(FunctionParam::toFunctionParameter).toList());
        }
    }

}
