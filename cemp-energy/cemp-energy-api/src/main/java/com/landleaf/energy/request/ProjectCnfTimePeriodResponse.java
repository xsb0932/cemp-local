package com.landleaf.energy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * ProjectCnfTimePeriodResponse
 *
 * @author 张力方
 * @since 2023/8/2
 **/
@Data
public class ProjectCnfTimePeriodResponse {
    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 时间段-年
     */
    @Schema(description = "时间段-年")
    private String periodYear;

    /**
     * 时间段-月
     */
    @Schema(description = "时间段-月")
    private String periodMonth;

    /**
     * 分时code
     */
    @Schema(description = "分时code")
    private String code;

    /**
     * 分时name
     */
    @Schema(description = "分时name")
    private String name;

    /**
     * 取值时间-开始
     */
    @Schema(description = "取值时间-开始")
    private Integer timeBegin;

    /**
     * 取值时间-结束
     */
    @Schema(description = "取值时间-结束")
    private Integer timeEnd;

    /**
     * 电价
     */
    @Schema(description = "电价")
    private BigDecimal price;
}
