package com.landleaf.gw.domain.screen.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 充电桩
 *
 * @author 张力方
 * @since 2023/7/28
 **/
@Data
public class ChargeResponse {
    /**
     * 当日充电功率
     */
    @Schema(description = "当日充电功率")
    private DayP dayP;
    /**
     * 当日充电量
     */
    @Schema(description = "当日充电量")
    private BigDecimal electricityStorageEnergyUsageCurrentDay;
    /**
     * 当月充电量
     */
    @Schema(description = "当月充电量")
    private BigDecimal electricityStorageEnergyUsageCurrentMonth;
    /**
     * 当年充电量
     */
    @Schema(description = "当年充电量")
    private BigDecimal electricityStorageEnergyUsageCurrentYear;
    /**
     * 当月收益
     */
    @Schema(description = "当月收益")
    private BigDecimal electricityStorageEnergyFeeCurrentMonth;
    /**
     * 当年收益
     */
    @Schema(description = "当年收益")
    private BigDecimal electricityStorageEnergyFeeCurrentYear;
    /**
     * 日充电趋势 key 天
     */
    @Schema(description = "日充电趋势")
    private DayTrend electricityStorageEnergyUsageDayTrend;
    /**
     * 月充电趋势 key 月
     */
    @Schema(description = "月充电趋势")
    private MonthTrend electricityStorageEnergyUsageMonthTrend;
    /**
     * 充电桩当年充电排名 key 设备名称
     */
    @Schema(description = "充电桩当年充电排名")
    private List<Device> electricityStorageEnergyUsageRanking;

    private Middle middle = new Middle();

    @Data
    public static class Middle {
        /**
         * 1# 交流桩
         */
        private Station alStation01 = new Station();
        /**
         * 2# 交流桩
         */
        private Station alStation02 = new Station();
        /**
         * 3# 交流桩
         */
        private Station alStation03 = new Station();
        /**
         * 4# 交流桩
         */
        private Station alStation04 = new Station();
        /**
         * 5# 交流桩
         */
        private Station alStation05 = new Station();
        /**
         * 1# 直流桩
         */
        private Station dirStation01 = new Station();

    }

    @Data
    public static class Station {
        /**
         * 充电状态
         */
        private String status;
        /**
         * 充电功率
         */
        private BigDecimal inChargeP;
        /**
         * 当日充电量
         */
        private BigDecimal inChargeCurrentDay;
    }

    @Data
    public static class DayP {
        /**
         * 时间
         */
        @Schema(description = "时间")
        private List<String> x = new ArrayList<>();
        /**
         * 功率
         */
        @Schema(description = "功率")
        private List<BigDecimal> p = new ArrayList<>();
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

    @Data
    public static class MonthTrend {
        /**
         * 月
         */
        @Schema(description = "月")
        private List<Integer> x = new ArrayList<>();
        /**
         * 值
         */
        @Schema(description = "值")
        private List<BigDecimal> value = new ArrayList<>();
    }

    @Data
    public static class Device {
        /**
         * 设备业务id
         */
        @Schema(description = "设备业务id")
        private String deviceBizId;
        /**
         * 值
         */
        @Schema(description = "值")
        private BigDecimal value;
    }
}
