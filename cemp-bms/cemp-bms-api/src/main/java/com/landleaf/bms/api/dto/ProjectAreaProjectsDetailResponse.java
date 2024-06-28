package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 区域-项目明细
 *
 * @author xushibai
 * @since 2024/6/12
 **/
@Data
@Schema(name = "区域-项目明细", description = "区域-项目明细")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAreaProjectsDetailResponse {
    @Schema(description = "项目业务编号")
    private String projectId;
    @Schema(description = "项目面积")
    private BigDecimal projectArea;
    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "指标数据")
    private BigDecimal consumption;
}
