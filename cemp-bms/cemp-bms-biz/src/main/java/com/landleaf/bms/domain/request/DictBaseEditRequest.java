package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑字典基本数据
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Data
@Schema(name = "编辑字典基本数据请求参数", description = "编辑字典基本数据请求参数")
public class DictBaseEditRequest {
    /**
     * 字典id
     */
    @NotNull(message = "字典id不能为空")
    @Schema(description = "字典id", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Long id;

    /**
     * 字典名称
     */
    @NotNull(message = "字典名称不能为空")
    @Size(min = 1, max = 50, message = "字典名称长度区间{min}-{max}")
    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理节点")
    private String name;

    /**
     * 字典描述
     */
    @NotNull(message = "字典描述不能为空")
    @Size( max = 255, message = "字典名称最大长度{max}")
    @Schema(description = "字典描述不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "todo")
    private String description;
}
