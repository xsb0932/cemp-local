package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ProjectCnfTimePeriodEntity对象的删除时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectCnfTimePeriodRmDTO对象", description = "ProjectCnfTimePeriodEntity对象的删除时的参数封装")
public class ProjectCnfTimePeriodRmDTO {
    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    @Schema(description = "时间：yyyy年MM月")
    private String time;
}
