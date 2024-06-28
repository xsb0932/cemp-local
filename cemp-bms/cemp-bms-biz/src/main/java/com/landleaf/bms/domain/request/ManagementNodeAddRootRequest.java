package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 新增根管理节点
 *
 * @author 张力方
 * @since 2023/6/5
 **/
@Data
@Schema(name = "新增根管理节点请求参数", description = "新增根管理节点请求参数")
public class ManagementNodeAddRootRequest {
    /**
     * 节点名称
     */
    @Schema(description = "节点名称", example = "01")
    private String name;

    /**
     * 节点code（校验唯一）
     */
    @Schema(description = "节点code（校验唯一）", example = "01")
    private String code;

}
