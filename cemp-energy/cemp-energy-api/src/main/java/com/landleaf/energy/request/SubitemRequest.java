package com.landleaf.energy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 分项指标查询
 *
 * @author xushibai
 * @since 2023/12/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分项指标查询")
public class SubitemRequest {

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    @NotEmpty(message = "项目ID不能为空")
    private String projectId;
    /**
     * 日期
     */
    @Schema(description = "租户ID")
    @NotNull(message = "租户ID")
    private Long tenantId ;

}
