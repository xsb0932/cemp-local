package com.landleaf.gw.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 光伏
 */
@Data
public class Pv {

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
     * 功率因素
     */
    private BigDecimal PF;
    /**
     * 效率
     */
    private BigDecimal E;


    /**
     * 输入功率
     */
    private BigDecimal PInput;
    /**
     * 当天峰值有功功率
     */
    private BigDecimal PMaxDay;
    /**
     * 反向有功总电能
     */
    private BigDecimal Epexp;

    /**
     * 当日发电量
     */
    private BigDecimal EpexpDay;
    /**
     * 累计发电量
     */
    private BigDecimal TEO;
    /**
     * 逆变器运行状态
     */
    private BigDecimal znbRST;
    /**
     * 内部温度
     */
    private BigDecimal Temperature;

    private BigDecimal PV1U;
    private BigDecimal PV2U;
    private BigDecimal PV3U;
    private BigDecimal PV4U;
    private BigDecimal PV5U;
    private BigDecimal PV6U;
    private BigDecimal PV7U;
    private BigDecimal PV8U;
    private BigDecimal PV9U;
    private BigDecimal PV10U;
    private BigDecimal PV11U;
    private BigDecimal PV12U;
    private BigDecimal PV13U;
    private BigDecimal PV14U;
    private BigDecimal PV15U;
    private BigDecimal PV16U;
    private BigDecimal PV17U;
    private BigDecimal PV18U;
    private BigDecimal PV19U;
    private BigDecimal PV20U;
    private BigDecimal PV1I;
    private BigDecimal PV2I;
    private BigDecimal PV3I;
    private BigDecimal PV4I;
    private BigDecimal PV5I;
    private BigDecimal PV6I;
    private BigDecimal PV7I;
    private BigDecimal PV8I;
    private BigDecimal PV9I;
    private BigDecimal PV10I;
    private BigDecimal PV11I;
    private BigDecimal PV12I;
    private BigDecimal PV13I;
    private BigDecimal PV14I;
    private BigDecimal PV15I;
    private BigDecimal PV16I;
    private BigDecimal PV17I;
    private BigDecimal PV18I;
    private BigDecimal PV19I;
    private BigDecimal PV20I;





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

        if (null != PF) {
            valMap.put("PF", PF);
        }
        if (null != znbRST) {
            valMap.put("znbRST", znbRST);
        }
        if (null != PInput) {
            valMap.put("PInput", PInput);
        }
        if (null != PMaxDay) {
            valMap.put("PMaxDay", PMaxDay);
        }
        if (null !=E ) {
            valMap.put("E", E);
        }
        if (null !=Epexp) {
            valMap.put("Epexp", Epexp);
        }
        if (null !=EpexpDay) {
            valMap.put("EpexpDay", EpexpDay);
        }
        if (null != TEO) {
            valMap.put("TEO", TEO);
        }
        if (null != Temperature) {
            valMap.put("Temperature",Temperature );
        }
        if (null != Temperature) {valMap.put("Temperature",Temperature );}
        if (null != PV1U) {valMap.put("PV1U",PV1U );}
        if (null != PV2U) {valMap.put("PV2U",PV2U );}
        if (null != PV3U) {valMap.put("PV3U",PV3U );}
        if (null != PV4U) {valMap.put("PV4U",PV4U );}
        if (null != PV5U) {valMap.put("PV5U",PV5U );}
        if (null != PV6U) {valMap.put("PV6U",PV6U );}
        if (null != PV7U) {valMap.put("PV7U",PV7U );}
        if (null != PV8U) {valMap.put("PV8U",PV8U );}
        if (null != PV9U) {valMap.put("PV9U",PV9U );}
        if (null != PV10U) {valMap.put("PV10U",PV10U );}
        if (null != PV11U) {valMap.put("PV11U",PV11U );}
        if (null != PV12U) {valMap.put("PV12U",PV12U );}
        if (null != PV13U) {valMap.put("PV13U",PV13U );}
        if (null != PV14U) {valMap.put("PV14U",PV14U );}
        if (null != PV15U) {valMap.put("PV15U",PV15U );}
        if (null != PV16U) {valMap.put("PV16U",PV16U );}
        if (null != PV17U) {valMap.put("PV17U",PV17U );}
        if (null != PV18U) {valMap.put("PV18U",PV18U );}
        if (null != PV19U) {valMap.put("PV19U",PV19U );}
        if (null != PV20U) {valMap.put("PV20U",PV20U );}
        if (null != PV1I) {valMap.put("PV1I",PV1I );}
        if (null != PV2I) {valMap.put("PV2I",PV2I );}
        if (null != PV3I) {valMap.put("PV3I",PV3I );}
        if (null != PV4I) {valMap.put("PV4I",PV4I );}
        if (null != PV5I) {valMap.put("PV5I",PV5I );}
        if (null != PV6I) {valMap.put("PV6I",PV6I );}
        if (null != PV7I) {valMap.put("PV7I",PV7I );}
        if (null != PV8I) {valMap.put("PV8I",PV8I );}
        if (null != PV9I) {valMap.put("PV9I",PV9I );}
        if (null != PV10I) {valMap.put("PV10I",PV10I );}
        if (null != PV11I) {valMap.put("PV11I",PV11I );}
        if (null != PV12I) {valMap.put("PV12I",PV12I );}
        if (null != PV13I) {valMap.put("PV13I",PV13I );}
        if (null != PV14I) {valMap.put("PV14I",PV14I );}
        if (null != PV15I) {valMap.put("PV15I",PV15I );}
        if (null != PV16I) {valMap.put("PV16I",PV16I );}
        if (null != PV17I) {valMap.put("PV17I",PV17I );}
        if (null != PV18I) {valMap.put("PV18I",PV18I );}
        if (null != PV19I) {valMap.put("PV19I",PV19I );}
        if (null != PV20I) {valMap.put("PV20I",PV20I );}

        return valMap;
    }
}
