package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用水费用配置表的查询时的参数封装
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectCnfWaterFeeQueryDTO", description = "用水费用配置表的查询时的参数封装")
public class ProjectCnfWaterFeeQueryDTO extends PageParam{

/**
 * 用水配置id
 */
        @Schema(description = "用水配置id")
    private Long id;

/**
 * 项目ID
 */
        @Schema(description = "项目ID")
    private String projectId;

/**
 * 收费模式，0=>单一价格
 */
        @Schema(description = "收费模式，0=>单一价格")
    private Integer chargingMode;

/**
 * 燃气单价
 */
        @Schema(description = "燃气单价")
    private BigDecimal price;

/**
 * 污水比例
 */
        @Schema(description = "污水比例")
    private BigDecimal sewageRatio;

/**
 * 污水处理价格
 */
        @Schema(description = "污水处理价格")
    private BigDecimal sewagePrice;

/**
 * 租户id
 */
        @Schema(description = "租户id")
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
