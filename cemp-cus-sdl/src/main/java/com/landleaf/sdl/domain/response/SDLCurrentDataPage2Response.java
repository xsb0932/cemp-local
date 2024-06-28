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
public class SDLCurrentDataPage2Response {
    /**
     * 交流侧-楼顶层有功功率
     */
    @Schema(description = "交流侧-楼顶层有功功率")
    private BigDecimal altrfP;
    /**
     * 交流侧-楼顶层无功功率
     */
    @Schema(description = "交流侧-楼顶层无功功率")
    private BigDecimal altrfQ;

    /**
     * 交流侧-4层有功功率
     */
    @Schema(description = "交流侧-4层有功功率")
    private BigDecimal altl4P;
    /**
     * 交流侧-4层无功功率
     */
    @Schema(description = "交流侧-4层无功功率")
    private BigDecimal altl4Q;

    /**
     * 交流侧-3层有功功率
     */
    @Schema(description = "交流侧-4层有功功率")
    private BigDecimal altl3P;
    /**
     * 交流侧-3层无功功率
     */
    @Schema(description = "交流侧-3层无功功率")
    private BigDecimal altl3Q;

    /**
     * 交流侧-2层有功功率
     */
    @Schema(description = "交流侧-2层有功功率")
    private BigDecimal altl2P;
    /**
     * 交流侧-2层无功功率
     */
    @Schema(description = "交流侧-2层无功功率")
    private BigDecimal altl2Q;

    /**
     * 交流侧-1层有功功率
     */
    @Schema(description = "交流侧-1层有功功率")
    private BigDecimal altl1P;
    /**
     * 交流侧-1层无功功率
     */
    @Schema(description = "交流侧-1层无功功率")
    private BigDecimal altl1Q;

    /**
     * 直流侧-楼顶层有功功率
     */
    @Schema(description = "直流侧-楼顶层有功功率")
    private BigDecimal dirrfP;
    /**
     * 直流侧-楼顶层无功功率
     */
    @Schema(description = "直流侧-楼顶层无功功率")
    private BigDecimal dirrfQ;

    /**
     * 直流侧-1层有功功率
     */
    @Schema(description = "交流侧-1层有功功率")
    private BigDecimal dirl1P;
    /**
     * 直流侧-2层有功功率
     */
    @Schema(description = "交流侧-2层有功功率")
    private BigDecimal dirl2P;
    /**
     * 直流侧-3层有功功率
     */
    @Schema(description = "交流侧-3层有功功率")
    private BigDecimal dirl3P;
    /**
     * 直流侧-4层有功功率
     */
    @Schema(description = "交流侧-4层有功功率")
    private BigDecimal dirl4P;



}
