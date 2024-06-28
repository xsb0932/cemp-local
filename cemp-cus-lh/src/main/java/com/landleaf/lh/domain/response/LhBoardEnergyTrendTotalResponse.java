package com.landleaf.lh.domain.response;

import com.landleaf.comm.vo.CommonStaVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author xusihbai
 * @since 2024/01/26
 **/
@Data
@Schema(description = "看板-项目综能耗趋势")
public class LhBoardEnergyTrendTotalResponse {

    @Schema(description = "项目综能耗趋势")
    private List<CommonStaVO> barChartData;

}
