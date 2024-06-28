package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

/**
 * 电费配置表的展示信息封装
 *
 * @author hebin
 * @since 2024-03-20
 */
@Data
@Schema(name = "ProjectCnfElectricityPriceVO", description = "电费配置表的展示信息封装")
public class ProjectCnfElectricityPriceVO {

    /**
     * 分时配置id
     */
    @Schema(description = "分时配置id")
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 电费类型，见字典electricity_price_type
     */
    @Schema(description = "电费类型，见字典electricity_price_type")
    private String type;

    /**
     * 电费类型描述
     */
    @Schema(description = "电费类型描述")
    private String typeDesc;

    /**
     * 电价
     */
    @Schema(description = "电价")
    private BigDecimal price;

    /**
     * 租户ID
     */
    @Schema(description = " 租户ID")
    private Long tenantId;

    /**
     * 分时电价配置
     */
    @Schema(description = "分时电价配置")
    private List<ProjectCnfTimePeriodVO> projectCnfTimePeriod;
}