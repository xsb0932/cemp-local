package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.landleaf.comm.base.pojo.PageParam;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 电费配置表的查询时的参数封装
 *
 * @author hebin
 * @since 2024-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectCnfElectricityPriceQueryDTO", description = "电费配置表的查询时的参数封装")
public class ProjectCnfElectricityPriceQueryDTO extends PageParam{

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
 * 电价
 */
        @Schema(description = "电价")
    private BigDecimal price;

/**
 *  租户ID
 */
        @Schema(description = " 租户ID")
    private Long tenantId;

/**
 * 开始时间
 */
@Schema(name = "开始时间,格式为yyyy-MM-dd")
private String startTime;

/**
 * 结束时间
 */
@Schema(name = "结束时间,格式为yyyy-MM-dd")
private String endTime;
        }