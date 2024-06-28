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
 * ProjectCnfTimePeriodEntity对象的查询时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ProjectCnfTimePeriodQueryDTO对象", description = "ProjectCnfTimePeriodEntity对象的查询时的参数封装")
public class ProjectCnfTimePeriodQueryDTO extends PageParam {

    /**
     * 分时配置id
     */
    @Schema(name = "分时配置id")
    private Long id;

    /**
     * 项目ID
     */
    @Schema(name = "项目ID")
    private String projectId;

    /**
     * 时间段-年
     */
    @Schema(name = "时间段-年")
    private String periodYear;

    /**
     * 时间段-月
     */
    @Schema(name = "时间段-月")
    private String periodMonth;

    /**
     * 分时code
     */
    @Schema(name = "分时code")
    private String code;

    /**
     * 分时name
     */
    @Schema(name = "分时name")
    private String name;

    /**
     * 取值时间-开始
     */
    @Schema(name = "取值时间-开始")
    private Integer timeBegin;

    /**
     * 取值时间-结束
     */
    @Schema(name = "取值时间-结束")
    private Integer timeEnd;

    /**
     * 电价
     */
    @Schema(name = "电价")
    private BigDecimal price;

    /**
     * 租户ID
     */
    @Schema(name = " 租户ID")
    private Long tennntId;

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