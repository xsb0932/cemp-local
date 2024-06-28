package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ManagementNodeCodeUniqueRequest
 *
 * @author 张力方
 * @since 2023/6/9
 **/
@Data
public class ManagementNodeCodeUniqueRequest {
    /**
     * 业务节点id
     * <p>
     * 编辑时不能为空
     */
    @Schema(name = "业务节点id", description = "编辑时不能为空", example = "01")
    private String bizNodeId;

    /**
     * 节点code（校验唯一）
     */
    @Schema(description = "节点code（校验唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    @NotBlank(message = "节点编码不能为空")
    private String code;

    /**
     * 租户id
     */
    @Schema(description = "租户id", example = "1")
    private Long tenantId;

}
