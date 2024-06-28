package com.landleaf.gw.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 储能
 */
@Data
public class Storage {

    /**
     * 设备编号
     */
    private String deviceId;

    /**
     * ab线电压
     */
    private BigDecimal Uab;

    /**
     * bc线电压
     */
    private BigDecimal Ubc;

    /**
     * ca线电压
     */
    private BigDecimal Uca;

    /**
     * a相电流
     */
    private BigDecimal Ia;

    /**
     * b相电流
     */
    private BigDecimal Ib;

    /**
     * c相电流
     */
    private BigDecimal Ic;

    /**
     * 频率
     */
    private BigDecimal F;

    /**
     * 无功功率
     */
    private BigDecimal Q;


    /**
     * 功率因素
     */
    private BigDecimal PF;

    /**
     * 正向有功总电能
     */
    private BigDecimal Epimp;

    /**
     * 反向有功总电能
     */
    private BigDecimal Epexp;

    /**
     *IGBT温度
     */
    private BigDecimal TempIGBT;
    /**
     * 电池的剩余容量
     */
    private BigDecimal SOC;
    /**
     * 电池健康状态
     */
    private BigDecimal SOH;
    /**
     * 电池电流
     */
    private BigDecimal BatteryI;
    /**
     *电池总压
     */
    private BigDecimal BatteryU;
    /**
     *
     */
    private BigDecimal DCU;
    /**
     *直流电压
     */
    private BigDecimal DCI;
    /**
     *直流侧电流
     */
    private BigDecimal DCP;

    /**
     * 有功功率
     */
    private BigDecimal P;
    /**
     *储能变流器运行状态
     */
    private BigDecimal pcsRST;

    /**
     *最低单体电压
     */
    private BigDecimal UnitMinU;
    /**
     *最高单体温度
     */
    private BigDecimal UnitMaxT;
    /**
     *最低单体温度
     */
    private BigDecimal UnitMinT;
    /**
     *最高单体电压
     */
    private BigDecimal UnitMaxU;
    /**
     *当日放电量
     */
    private BigDecimal dayDischargeE;
    /**
     *当日充电量
     */
    private BigDecimal dayChargeE;






    public Map<String, Object> toMap() {
        Map<String, Object> valMap = Maps.newHashMap();
        if (null != UnitMinU) {
            valMap.put("UnitMinU", UnitMinU);
        }
        if (null != UnitMaxT) {
            valMap.put("UnitMaxT", UnitMaxT);
        }
        if (null != UnitMinT) {
            valMap.put("UnitMinT", UnitMinT);
        }
        if (null != UnitMaxU) {
            valMap.put("UnitMaxU", UnitMaxU);
        }
        if (null != dayDischargeE) {
            valMap.put("dayDischargeE", dayDischargeE);
        }
        if (null != dayChargeE) {
            valMap.put("dayChargeE", dayChargeE);
        }

        if (null != Uab) {
            valMap.put("Uab", Uab);
        }
        if (null != Ubc) {
            valMap.put("Ubc", Ubc);
        }
        if (null != Uca) {
            valMap.put("Uca", Uca);
        }
        if (null != Ia) {
            valMap.put("Ia", Ia);
        }
        if (null != Ib) {
            valMap.put("Ib", Ib);
        }
        if (null != Ic) {
            valMap.put("Ic", Ic);
        }
        if (null != F) {
            valMap.put("F", F);
        }
        if (null != P) {
            valMap.put("P", P);
        }
        if (null != Q) {
            valMap.put("Q", Q);
        }
        if (null != PF) {
            valMap.put("PF", PF);
        }
        if (null != TempIGBT) {
            valMap.put("TempIGBT", TempIGBT);
        }
        if (null != SOC) {
            valMap.put("SOC", SOC);
        }
        if (null != SOH) {
            valMap.put("SOH", SOH);
        }
        if (null != BatteryI) {
            valMap.put("BatteryI", BatteryI);
        }
        if (null != BatteryU) {
            valMap.put("BatteryU", BatteryU);
        }
        if (null != DCU) {
            valMap.put("DCU", DCU);
        }
        if (null != DCI) {
            valMap.put("DCI", DCI);
        }
        if (null != DCP) {
            valMap.put("DCP", DCP);
        }
        if (null != pcsRST) {
            valMap.put("pcsRST", pcsRST);
        }
        if (null != Epexp) {
            valMap.put("Epexp", Epexp);
        }
        if (null != Epimp) {
            valMap.put("Epimp", Epimp);
        }

        return valMap;
    }
}
