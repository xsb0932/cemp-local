package com.landleaf.energy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

/**
 * 分区指标查询
 *
 * @author xushibai
 * @since 2024/6/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分区指标查询")
public class SubareaMonthRequest {

    @Schema(description = "项目ID")
    private String projectId;

    @Schema(description = "租户ID")
    private Long tenantId ;

    @Schema(description = "查询月份")
    private List<YearMonth> yms;

    @Schema(description = "查询指标")
    private String kpi;

    @Schema(description = "分区name")
    private List<String> subareaName;

    @Schema(description = "kpi类型")
    private String kpiType;

}
