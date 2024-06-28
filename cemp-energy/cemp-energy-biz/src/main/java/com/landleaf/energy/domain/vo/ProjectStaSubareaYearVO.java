package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * ProjectStaSubareaYearEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProjectStaSubareaYearVO对象", description = "ProjectStaSubareaYearEntity对象的展示信息封装")
public class ProjectStaSubareaYearVO {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 指标CODE
     */
    @Schema(description = "指标CODE")
    private String kpiCode;

    /**
     * 分区代码
     */
    @Schema(description = "分区代码")
    private String subareaCode;

    /**
     * 分区名字
     */
    @Schema(description = "分区名字")
    private String subareaName;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 项目代码
     */
    @Schema(description = "项目代码")
    private String projectCode;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 租户代码
     */
    @Schema(description = "租户代码")
    private String tenantCode;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 统计-年
     */
    @Schema(description = "统计-年")
    private String year;

    /**
     * 统计值
     */
    @Schema(description = "统计值")
    private BigDecimal staValue;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;

    public ProjectStaSubareaYearVO(String kpiCode, String subareaCode, String subareaName, String bizProjectId) {
        this.kpiCode = kpiCode;
        this.subareaCode = subareaCode;
        this.subareaName = subareaName;
        this.bizProjectId = bizProjectId;
    }
}
