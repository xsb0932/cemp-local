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
@Schema(description = "看板-项目单方(单平，单立)空调用电趋势对比")
public class LhBoardHavcEnergyCompareResponse {

    @Schema(description = "项目单方空调用电趋势对比")
    private List<CommonStaVO> barChartData;

}
