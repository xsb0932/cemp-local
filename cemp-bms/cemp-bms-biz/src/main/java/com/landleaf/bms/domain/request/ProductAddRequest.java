package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import javax.annotation.Nullable;

/**
 * 新增产品请求参数
 *
 * @author 张力方
 * @since 2023/7/3
 **/
@Data
@Schema(name = "新增产品请求参数", description = "新增产品请求参数")
public class ProductAddRequest {
    /**
     * 所属品类id
     */
    @NotBlank(message = "所属品类不能为空")
    @Schema(description = "所属品类id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String categoryId;
    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(min = 1, max = 50, message = "产品名称长度区间{min}-{max}")
    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String name;
    /**
     * 产品型号
     */
    @NotBlank(message = "产品型号不能为空")
    @Size(min = 1, max = 50, message = "产品型号长度区间{min}-{max}")
    @Schema(description = "产品型号", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String model;
    /**
     * 产品厂商
     */
    @Schema(description = "产品厂商", example = "XXX")
    private String factory;
    /**
     * 是否实体产品
     */
    @NotNull(message = "是否实体产品不能为空")
    @Schema(description = "是否实体产品", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isReal;
    /**
     * 联网方式 - 数据字典 PRODUCT_COMMUNICATION_TYPE
     */
    @NotNull(message = "联网方式不能为空")
    @Schema(description = "联网方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "XXX")
    private String communicationType;
    /**
     * 是否长连设备
     */
    @NotNull(message = "是否长连设备不能为空")
    @Schema(description = "是否长连设备", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isLongOnline;
    /**
     * 异常超时
     */
    @NotNull(message = "异常超时不能为空")
    @Schema(description = "异常超时", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Double timeout;
    /**
     * 产品描述
     */
    @Size(max = 255, message = "产品描述长度应小于{max}")
    @Schema(description = "产品描述", example = "XXX")
    private String description;
}
