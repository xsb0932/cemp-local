package com.landleaf.jzd.domain.response;

import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.jzd.domain.vo.JzdBarCartData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 实时数据
 *
 * @author xusihbai
 * @since 2024/01/22
 **/
@Data
public class JzdCurrentDatResponse {

    @Schema(description = "电网P")
    private String eleNetP;
    @Schema(description = "电网Q")
    private String eleNetQ;
    @Schema(description = "光伏发电P")
    private BigDecimal pvP;
    @Schema(description = "光伏发电Q")
    private BigDecimal pvQ;
    @Schema(description = "当月购电量")
    private BigDecimal purchaseMonth;
    @Schema(description = "当月发电量")
    private BigDecimal epexportMonth;
    @Schema(description = "当月上网电量")
    private BigDecimal eleGrid;

    @Schema(description = "电梯P")
    private BigDecimal elevatorP;
    @Schema(description = "电梯Q")
    private BigDecimal elevatorQ;
    @Schema(description = "电梯当月用电量")
    private BigDecimal elevatorEpimportMonth;

    @Schema(description = "插座照明P")
    private BigDecimal lightP;
    @Schema(description = "插座照明Q")
    private BigDecimal lightQ;
    @Schema(description = "插座照明当月用电量")
    private BigDecimal lightEpimportMonth;

    @Schema(description = "空调P")
    private BigDecimal havcP;
    @Schema(description = "空调Q")
    private BigDecimal havcQ;
    @Schema(description = "空调当月用电量")
    private BigDecimal havcEpimportMonth;

    @Schema(description = "其他P")
    private BigDecimal otherP;
    @Schema(description = "其他Q")
    private BigDecimal otherQ;
    @Schema(description = "其他当月用电量")
    private BigDecimal otherEpimportMonth;






}
