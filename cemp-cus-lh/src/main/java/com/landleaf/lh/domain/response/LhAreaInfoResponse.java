package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xusihbai
 * @since 2024/06/11
 **/
@Data
@Schema(description = "区域列表")
@AllArgsConstructor
public class LhAreaInfoResponse {

    @Schema(description = "区域节点id")
    private String areaId;
    @Schema(description = "区域名称")
    private String areaName;

}
