package com.landleaf.sdl.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 总览
 *
 * @author xusihbai
 * @since 2023/11/29
 **/
@Data
public class SDLCurrentDataPage1Response {
    /**
     * 电网P
     */
    @Schema(description = "电网P")
    private BigDecimal eleNetP;

    /**
     * 电网Q
     */
    @Schema(description = "电网Q")
    private BigDecimal eleNetQ;

    /**
     * 交流P
     */
    @Schema(description = "交流P")
    private BigDecimal eleALtP;

    /**
     * 交流Q
     */
    @Schema(description = "交流Q")
    private BigDecimal eleALtQ;

    /**
     * 储能P
     */
    @Schema(description = "储能P")
    private BigDecimal storageP;

    /**
     * 光伏P
     */
    @Schema(description = "光伏P")
    private BigDecimal pvP;

    /**
     * 充电桩P
     */
    @Schema(description = "充电桩P")
    private BigDecimal chargeP;

    /**
     * 直流负荷P
     */
    @Schema(description = "直流负荷P")
    private BigDecimal dbusP;

    /**
     * 储能总电压
     */
    @Schema(description = "储能总电压")
    private BigDecimal storageVol;

    /**
     * 储能SOC
     */
    @Schema(description = "储能SOC")
    private BigDecimal storageSOC;

    /**
     * 直流母线电压
     */
    @Schema(description = "直流母线电压")
    private BigDecimal fcsVol;

    /**
     * 电网当日购网电量
     */
    @Schema(description = "电网当日购网电量")
    private BigDecimal epimportTodayTotal;

    /**
     * FCS系统柔性控制 当日用电量
     */
    @Schema(description = "FCS系统柔性控制 当日用电量")
    private BigDecimal epimportTodayFCS;

    /**
     *  FCS系统柔性控制 当日发电量
     */
    @Schema(description = "FCS系统柔性控制 当日发电量")
    private BigDecimal epexportTodayFCS;

    /**
     * 交流系统当日用电量
     */
    @Schema(description = "交流系统当日用电量")
    private BigDecimal epimportTodayAlt;

    /**
     * 光伏当日发电量： 光伏回路电表的当日发电量
     */
    @Schema(description = "光伏当日发电量： 光伏回路电表的当日发电量")
    private BigDecimal epexportTodayPV;

    /**
     * 充电桩当日用电量： 充电回路电流的当日用电量
     */
    @Schema(description = "充电桩当日用电量： 充电回路电流的当日用电量")
    private BigDecimal epimportTodayCharge;

    /**
     * 直流负荷当日用电量： 220 V直流母线电表的当日用电量
     */
    @Schema(description = "直流负荷当日用电量： 220 V直流母线电表的当日用电量")
    private BigDecimal epimportTodayDbus;


}
