package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 指标库表的查询时的参数封装
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectKpiConfigQueryDTO", description = "指标库表的查询时的参数封装")
public class ProjectKpiConfigQueryDTO extends PageParam{

/**
 * 指标ID
 */
        @Schema(description = "指标ID")
    private Long id;

/**
 * 指标code
 */
        @Schema(description = "指标code")
    private String code;

/**
 * 指标名称
 */
        @Schema(description = "指标名称")
    private String name;

/**
 * 分项大类 水 气 电 ...
 */
        @Schema(description = "分项大类 水 气 电 ...")
    private String kpiType;

/**
 * 分项类型代码
 */
        @Schema(description = "分项类型代码")
    private String kpiSubtype;

/**
 * 统计间隔-小时-1:是 0 否
 */
        @Schema(description = "统计间隔-小时-1:是 0 否")
    private Integer staIntervalHour;

/**
 * 统计间隔-日月年-1:是 0 否
 */
        @Schema(description = "统计间隔-日月年-1:是 0 否")
    private Integer staIntervalYmd;

/**
 * 租户id
 */
        @Schema(description = "租户id")
    private Long tenantId;

/**
 * 单位
 */
        @Schema(description = "单位")
    private String unit;

/**
 * 分项大类代码
 */
        @Schema(description = "分项大类代码")
    private String kpiTypeCode;

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
