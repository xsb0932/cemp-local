package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 区域项目概况
 *
 * @author xushibai
 * @since 2024/6/12
 **/
@Data
@Schema(name = "区域项目概况", description = "区域项目概况")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAreaResponse {
    @Schema(description = "项目数量")
    private String projectNum;
    @Schema(description = "项目面积")
    private String projectArea;
}
