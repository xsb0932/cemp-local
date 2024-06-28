package com.landleaf.jzd.domain.response;

import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.jzd.domain.vo.JzdBarCartData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 总览数据
 *
 * @author xusihbai
 * @since 2024/01/22
 **/
@Data
public class JzdOverviewResponse {

    /** title **/
    @Schema(description = "当年购网电量")
    private BigDecimal electricityPccEnergyUsageCurrentYear;
    @Schema(description = "当年发电量")
    private BigDecimal electricityPvEnergyProductionCurrentYear;
    @Schema(description = "当年用电量")
    private BigDecimal eleUseCurrentYear;
    @Schema(description = "当年用水量")
    private BigDecimal waterUseCurrentYear;
    @Schema(description = "上年同期用水量")
    private BigDecimal waterUseLastYear;
    @Schema(description = "当年二氧化碳排量")
    private BigDecimal carbonUseCurrentYear;
    @Schema(description = "上月发电量")
    private BigDecimal elePvLastMonth;
    @Schema(description = "当月发电量")
    private BigDecimal elePvCurrentMonth;
    @Schema(description = "当月用电量")
    private BigDecimal eleUseCurrentMonth;
    @Schema(description = "当月用水量")
    private BigDecimal waterUseCurrentMonth;
    @Schema(description = "当年减少二氧化碳")
    private BigDecimal carbonReCurrentYear;
    @Schema(description = "用电计划")
    private BigDecimal elePlan;
    @Schema(description = "实际用电量")
    private BigDecimal eleTotal;
    @Schema(description = "当年上网电量")
    private BigDecimal eleGridCurrentYear;



    /** 暖通能耗用电强度 **/
    @Schema(description = "当年实际能耗强度(截止上月)")
    private BigDecimal energyActualCurrentYear;
    @Schema(description = "上年全年实际能耗强度")
    private BigDecimal energyActualLastYear;

    /** 当年用电自给比例&光伏消纳比例 **/
    @Schema(description = "光伏发电占总用电比例")
    private BigDecimal pvTotalRatio;
    @Schema(description = "光伏发电消纳比例")
    private BigDecimal pvConsRatio;


    /** 月电源结构趋势 **/
    @Schema(description = "月电源结构趋势")
    private JzdBarCartData eleMonthRatioTrend;
    /** 当年用电量占比 **/
    @Schema(description = "当年用电量占比")
    private CommonStaVO eleUseRatio;

    /** 月负荷结构趋势-电 **/
    @Schema(description = "月负荷结构趋势-电")
    private JzdBarCartData pEleMonthRatioTrend;

    /** 月负荷结构趋势-水 **/
    @Schema(description = "月负荷结构趋势-水")
    private JzdBarCartData pWaterEleMonthRatioTrend;

    /** 当年用水量占比 **/
    @Schema(description = "当年用水量占比")
    private CommonStaVO waterUseRatio;




}
