package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AreaManageNodeResponse
 *
 * @author xushibai
 * @since 2024/6/12
 **/
@Data
@Schema(description = "区域对象")
@AllArgsConstructor
@NoArgsConstructor
public class AreaManageNodeResponse {
    @Schema(description = "区域节点id")
    private String areaId;
    @Schema(description = "区域名称")
    private String areaName;
}
