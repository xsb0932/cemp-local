package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 编辑管理节点
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Data
@Schema(name = "编辑管理节点请求参数", description = "编辑管理节点请求参数")
public class ManagementNodeEditRequest {
    /**
     * 节点业务id
     */
    @NotBlank(message = "节点业务id不能为空")
    @Schema(description = "节点业务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    private String bizNodeId;

    /**
     * 节点名称
     */
    @NotBlank(message = "节点名称不能为空")
    @Schema(description = "节点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    private String name;

    /**
     * 节点code（校验唯一）
     */
    @NotBlank(message = "节点编码不能为空")
    @Schema(description = "节点code（校验唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    private String code;

    /**
     * 父节点业务id
     */
    @NotBlank(message = "父节点业务id不能为空")
    @Schema(description = "父节点业务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    private String parentBizNodeId;

    /**
     * 节点类型（字典类型-管理节点）
     * <p>
     * 企业 - 01
     * 项目 - 00
     * 城市 - 02
     * <p>
     * 企业和项目不作为新增节点类型选项
     */
    @NotBlank(message = "节点类型不能为空")
    @Schema(description = "节点类型（字典类型-管理节点）", requiredMode = Schema.RequiredMode.REQUIRED, example = "01")
    private String type;

    /**
     * 租户id
     */
    @Schema(description = "租户id", example = "01")
    private Long tenantId;

}
