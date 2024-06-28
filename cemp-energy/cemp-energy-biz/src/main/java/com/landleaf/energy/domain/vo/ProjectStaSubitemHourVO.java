package com.landleaf.energy.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * ProjectStaSubitemHourEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectStaSubitemHourVO对象", description = "ProjectStaSubitemHourEntity对象的展示信息封装")
public class ProjectStaSubitemHourVO {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 项目CODE
     */
    @Schema(description = "项目CODE")
    private String projectCode;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 租户CODE
     */
    @Schema(description = "租户CODE")
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
     * 统计-月
     */
    @Schema(description = "统计-月")
    private String month;

    /**
     * 统计-天
     */
    @Schema(description = "统计-天")
    private String day;

    /**
     * 统计小时
     */
    @Schema(description = "统计小时")
    private String hour;

    /**
     * 总用气量
     */
    @Schema(description = "总用气量")
    private BigDecimal projectGasUsageTotal;

    /**
     * 总用水量
     */
    @Schema(description = "总用水量")
    private BigDecimal projectWaterUsageTotal;

    /**
     * 全部负荷总用电量
     */
    @Schema(description = "全部负荷总用电量")
    private BigDecimal projectElectricityEnergyusageTotal;

    /**
     * 电梯总用电量
     */
    @Schema(description = "电梯总用电量")
    private BigDecimal projectElectricitySubelevatorenergyTotal;

    /**
     * 客房总用电量
     */
    @Schema(description = "客房总用电量")
    private BigDecimal projectElectricitySubguestroomenergyTotal;

    /**
     * 热水总用电量
     */
    @Schema(description = "热水总用电量")
    private BigDecimal projectElectricitySubheatingwaterenergyTotal;

    /**
     * 空调总用电量
     */
    @Schema(description = "空调总用电量")
    private BigDecimal projectElectricitySubhavcenergyTotal;

    /**
     * 其他总用电量
     */
    @Schema(description = "其他总用电量")
    private BigDecimal projectElectricitySubothertypeTotal;

    /**
     * 供水总用电量
     */
    @Schema(description = "供水总用电量")
    private BigDecimal projectElectricitySubwatersupplyenergyTotal;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;


    /**
     * 空调补水
     * project.water.HVACUsage.total
     */
    private BigDecimal projectWaterHvacusageTotal;

    /**
     * 热水补水
     * project.water.HeatingWaterUsage.total
     */
    private BigDecimal projectWaterHeatingwaterusageTotal;

}
