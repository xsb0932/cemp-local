package com.landleaf.jjgj.domain.dto;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DeviceCategoryKpiConfigEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "DeviceCategoryKpiConfigQueryDTO", description = "DeviceCategoryKpiConfigEntity对象的查询时的参数封装")
public class DeviceCategoryKpiConfigQueryDTO extends PageParam{

/**
 * 指标ID
 */
        @Schema(description = "指标ID")
    private Long id;

/**
 * 指标代码
 */
        @Schema(description = "指标代码")
    private String code;

/**
 * 指标名称
 */
        @Schema(description = "指标名称")
    private String name;

/**
 * 品类id
 */
        @Schema(description = "品类id")
    private String bizCategoryId;

/**
 * 品类名称
 */
        @Schema(description = "品类名称")
    private String categoryName;

/**
 * 分项大类代码
 */
        @Schema(description = "分项大类代码")
    private String kpiTypeCode;

/**
 * 分项大类 水 气 电 ...
 */
        @Schema(description = "分项大类 水 气 电 ...")
    private String kpiType;

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
 * 单位
 */
        @Schema(description = "单位")
    private String unit;

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
