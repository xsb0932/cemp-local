package com.landleaf.sdl.domain.vo;

import com.landleaf.comm.vo.CommonStaVO;
import com.landleaf.energy.response.SubitemYearRatioResoponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 总览
 *
 * @author xusihbai
 * @since 2023/11/29
 **/
@Data
public class SDLEnergyMonthRatio {
    @Schema(description = "柱状图")
    private List<CommonStaVO> barChartData;
}
