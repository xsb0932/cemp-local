package com.landleaf.lh.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xusihbai
 * @since 2024/06/11
 **/
@Data
@Schema(description = "项目信息")
@AllArgsConstructor
public class LhAreaProjectInfoResponse {

    @Schema(description = "项目数量")
    private String projectNum;
    @Schema(description = "项目面积")
    private String projectArea;

}
