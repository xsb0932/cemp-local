package com.landleaf.sdl.domain.response;

import com.landleaf.energy.response.SubitemYearRatioResoponse;
import com.landleaf.sdl.domain.vo.SDLCommonStaVO;
import com.landleaf.sdl.domain.vo.SDLEnergyMonthRatio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 总览
 *
 * @author xusihbai
 * @since 2023/11/29
 **/
@Data
public class SDLOverviewPage2Response {
    /**
     * 直流照明
     */
    @Schema(description = "直流照明")
    private String lightNum;

    /**
     * 直流充电桩
     */
    @Schema(description = "直流充电桩")
    private String chargeNum;

    /**
     * 直流风盘
     */
    @Schema(description = "直流风盘")
    private String windboardNum;

    /**
     * 空调主机
     */
    @Schema(description = "空调主机")
    private String hostNum;

    /**
     * 电梯
     */
    @Schema(description = "电梯")
    private String elevatorNum;

    /**
     * 打印机
     */
    @Schema(description = "打印机")
    private String printNum;

    /**
     * 当日交直流负荷曲线
     */
    @Schema(description = "当日交直流负荷曲线")
    private SDLCommonStaVO daP;


    /**
     * 当年用电量
     */
    @Schema(description = "当年用电量")
    private SDLCommonStaVO pDay30;

    /**
     * 当月用电量
     */
    @Schema(description = "当月用电量")
    private SDLCommonStaVO pMonth12;

    /**
     * 当日用电量
     */
    @Schema(description = "当日用电量")
    private BigDecimal pDay;

    /**
     * 当月用电量
     */
    @Schema(description = "当月用电量")
    private BigDecimal pMonth;

    /**
     * 当年用电量
     */
    @Schema(description = "当年用电量")
    private BigDecimal pYear;

    /**
     * 当年电度电费
     */
    @Schema(description = "当年电度电费")
    private BigDecimal feeYear;

    /**
     * 当月电度电费
     */
    @Schema(description = "当月电度电费")
    private BigDecimal feeMonth;

    /**
     * 当月分区用电排名
     */
    @Schema(description = "当月分区用电排名")
    private SDLCommonStaVO subareaOrder;

    /**
     * 交直流负荷结构
     */
    @Schema(description = "交直流负荷结构")
    private SDLCommonStaVO paRatio;

    /**
     * 分类负荷结构
     */
    @Schema(description = "分类负荷结构")
    private SDLCommonStaVO paSubitem;

    @Schema(description = "当日交直流负荷曲线")
    private DayP dayP;

    @Data
    public static class DayP {
        /**
         * 时间
         */
        @Schema(description = "时间")
        private List<String> x = new ArrayList<>();
        /**
         * 直流负载
         */
        @Schema(description = "直流负载")
        private List<BigDecimal> pd = new ArrayList<>();
        /**
         * 交流负载
         */
        @Schema(description = "交流负载")
        private List<BigDecimal> pa = new ArrayList<>();
    }



}
