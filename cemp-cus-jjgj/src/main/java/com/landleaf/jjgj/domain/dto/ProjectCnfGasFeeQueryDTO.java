package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 燃气费用配置表的查询时的参数封装
 *
 * @author hebin
 * @since 2023-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectCnfGasFeeQueryDTO", description = "燃气费用配置表的查询时的参数封装")
public class ProjectCnfGasFeeQueryDTO extends PageParam{

/**
 * 燃气配置id
 */
        @Schema(description = "燃气配置id")
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
