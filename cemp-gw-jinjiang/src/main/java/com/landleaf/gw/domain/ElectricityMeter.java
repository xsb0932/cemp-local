package com.landleaf.gw.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 电表的信息封装
 */
@Data
public class ElectricityMeter {

    /**
     * 设备编号
     */
    private String deviceId;

    /**
     * a相电压
     */
    private BigDecimal Ua;

    /**
     * b相电压
     */
    private BigDecimal Ub;

    /**
     * c相电压
     */
    private BigDecimal Uc;

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
     * 有功功率
     */
    private BigDecimal P;

    /**
     * 无功功率
     */
    private BigDecimal Q;

    /**
     * 视在功率
     */
    private BigDecimal S;

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
     * 正向无功总电能
     */
    private BigDecimal Eqimp;

    /**
     * 反向无功总电能
     */
    private BigDecimal Eqexp;

    public Map<String, Object> toMap() {
        Map<String, Object> valMap = Maps.newHashMap();
        if (null != Ua) {
            valMap.put("Ua", Ua);
        }
        if (null != Ub) {
            valMap.put("Ub", Ub);
        }
        if (null != Uc) {
            valMap.put("Uc", Uc);
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
        if (null != S) {
            valMap.put("S", S);
        }
        if (null != PF) {
            valMap.put("PF", PF);
        }
        if (null != Epimp) {
            valMap.put("Epimp", Epimp);
        }
        if (null != Epexp) {
            valMap.put("Epexp", Epexp);
        }
        if (null != Eqimp) {
            valMap.put("Eqimp", Eqimp);
        }
        if (null != Eqexp) {
            valMap.put("Eqexp", Eqexp);
        }
        return valMap;
    }
}
