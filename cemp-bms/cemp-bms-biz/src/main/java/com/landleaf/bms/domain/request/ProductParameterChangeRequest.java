package com.landleaf.bms.domain.request;

import com.landleaf.bms.domain.entity.ProductParameterEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品参数变更请求参数
 *
 * @author yue lin
 * @since 2023/6/25 15:43
 */
public class ProductParameterChangeRequest {

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "产品参数新增请求参数")
    public static class Create extends ValueDescriptionParam {

        /**
         * id
         * 新增必须为空，更新必须不为空
         */
        @Null(groups = com.landleaf.web.validation.Create.class, message = "id必须为空")
        @NotNull(groups = com.landleaf.web.validation.Create.class, message = "id不能为空")
        @Schema(description = "id（新增必须为空，更新必须不为空）")
        private Long id;

        /**
         * 功能标识符
         */
        @NotBlank(message = "功能标识符不能为空")
        @Schema(description = "功能标识符")
        private String identifier;

        /**
         * 功能类别-（数据字典 PRODUCT_FUNCTION_CATEGORY）-产品参数
         */
        @NotBlank(message = "功能类别不能为空")
        @Schema(description = "功能类别", example = "产品参数")
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

        public ProductParameterEntity toEntity() {
            validate();
            ProductParameterEntity productParameterEntity = new ProductParameterEntity();
            productParameterEntity.setId(this.id);
            productParameterEntity.setIdentifier(this.identifier);
            productParameterEntity.setFunctionCategory(this.functionCategory);
            productParameterEntity.setFunctionName(this.functionName);
            productParameterEntity.setFunctionType(this.functionType);
            productParameterEntity.setDataType(getDataType());
            productParameterEntity.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
            return productParameterEntity;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Schema(description = "产品参数更新请求参数（数据类型只用来做校验值描述）")
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

        public void mergeEntity(ProductParameterEntity productParameterEntity) {
            validate();
            productParameterEntity.setFunctionName(this.functionName);
            productParameterEntity.setDataType(getDataType());
            productParameterEntity.setValueDescription(getValueDescription().stream().map(ValueAccount::toValueDescription).toList());
        }
    }

}
