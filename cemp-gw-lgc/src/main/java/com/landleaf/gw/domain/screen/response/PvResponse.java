package com.landleaf.gw.domain.screen.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 光伏大屏
 *
 * @author 张力方
 * @since 2023/7/28
 **/
@Data
public class PvResponse {
    /**
     * 绿电自给比例 %
     */
    @Schema(description = "绿电自给比例 %")
    private BigDecimal pvVsTotalPercentageCurrentYear;
    /**
     * 发电实时总功率
     */
    @Schema(description = "发电实时总功率")
    private BigDecimal currentTotalP;
    /**
     * 当年消纳比例 %
     */
    @Schema(description = "当年消纳比例 %")
    private BigDecimal pvAbsorptivePercentageCurrentYear;
    /**
     * 日发电功率 key 时间
     */
    @Schema(description = "日发电功率")
    private DayP dayP;
    /**
     * 当月收益
     */
    @Schema(description = "当月收益")
    private BigDecimal electricityPvEnergyProductionFeeCurrentMonth;
    /**
     * 当年收益
     */
    @Schema(description = "当年收益")
    private BigDecimal electricityPvEnergyProductionFeeCurrentYear;
    /**
     * 当年总发电
     */
    @Schema(description = "当年总发电")
    private BigDecimal electricityPvEnergyProductionCurrentYear;
    /**
     * 当年上网电量
     */
    @Schema(description = "当年上网电量")
    private BigDecimal electricityPvEnergyProductionGridCurrentYear;
    /**
     * 当年直接使用
     */
    @Schema(description = "当年直接使用")
    private BigDecimal electricityPvEnergyProductionLoadCurrentYear;
    /**
     * 当年先储后用
     */
    @Schema(description = "当年先储后用")
    private BigDecimal electricityPvEnergyProductionStorageCurrentYear;
    /**
     * 日发电 key 天
     */
    @Schema(description = "日发电")
    private DayTrend dayPvEnergyProduction;
    /**
     * 月发电 key 月
     */
    @Schema(description = "月发电")
    private PvProduction monthPvEnergyProduction;

    private Middle middle = new Middle();

    @Data
    public static class Middle {
        /**
         * 逆变器一
         */
        private Znb01 znb01 = new Znb01();
        /**
         * 逆变器二
         */
        private Znb02 znb02 = new Znb02();
    }

    @Data
    public static class Znb01 {
        private Pv pv1;
        private Pv pv2;
        private Pv pv3;
        private Pv pv4;
        private Pv pv5;
        private Pv pv6;
        private Pv pv7;
        private Pv pv8;
        private Pv pv9;
        private Pv pv10;
        private Pv pv11;

        /**
         * 效率
         */
        private BigDecimal efficiency;
        /**
         * 温度
         */
        private BigDecimal temp;
        private BigDecimal ua;
        private BigDecimal ia;
        private BigDecimal ub;
        private BigDecimal ib;
        private BigDecimal uc;
        private BigDecimal ic;
        private BigDecimal p;
        private BigDecimal q;
        /**
         * 输入功率
         */
        private BigDecimal pInput;
        /**
         * 当日发电量
         */
        private BigDecimal currentElectricity;
    }

    @Data
    public static class Znb02 {
        private Pv pv1;
        private Pv pv2;

        /**
         * 效率
         */
        private BigDecimal efficiency;
        /**
         * 温度
         */
        private BigDecimal temp;
        private BigDecimal ua;
        private BigDecimal ia;
        private BigDecimal ub;
        private BigDecimal ib;
        private BigDecimal uc;
        private BigDecimal ic;
        private BigDecimal p;
        private BigDecimal q;
        /**
         * 输入功率
         */
        private BigDecimal pInput;
        /**
         * 当日发电量
         */
        private BigDecimal currentElectricity;
    }

    @Data
    @AllArgsConstructor
    public static class Pv {
        /**
         * 电压
         */
        private BigDecimal u;
        /**
         * 电流
         */
        private BigDecimal i;
    }


    @Data
    public static class DayP {
        /**
         * 时间
         */
        @Schema(description = "时间")
        private List<String> x = new ArrayList<>();
        /**
         * 当日功率
         */
        @Schema(description = "当日功率")
        private List<BigDecimal> todayP = new ArrayList<>();
        /**
         * 昨日功率
         */
        @Schema(description = "昨日功率")
        private List<BigDecimal> yesterdayP = new ArrayList<>();
    }

    @Data
    public static class PvProduction {
        /**
         * 月
         */
        @Schema(description = "月")
        private List<Integer> x = new ArrayList<>();
        /**
         * 当年发电量
         */
        @Schema(description = "当年发电量")
        private List<BigDecimal> currentYear = new ArrayList<>();
        /**
         * 上年发电量
         */
        @Schema(description = "上年发电量")
        private List<BigDecimal> lastYear = new ArrayList<>();
        /**
         * 消纳率
         */
        @Schema(description = "消纳率")
        private List<BigDecimal> absorptivePercentage = new ArrayList<>();
    }

    @Data
    public static class DayTrend {
        /**
         * 天
         */
        @Schema(description = "天")
        private List<Integer> x = new ArrayList<>();
        /**
         * 值
         */
        @Schema(description = "值")
        private List<BigDecimal> value = new ArrayList<>();
    }
}
