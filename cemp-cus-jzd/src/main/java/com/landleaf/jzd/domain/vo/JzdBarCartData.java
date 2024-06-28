package com.landleaf.jzd.domain.vo;

import com.landleaf.comm.vo.CommonStaVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 金智达柱状图数据结构
 *
 * @author xusihbai
 * @since 2024/01/22
 **/
@Data
public class JzdBarCartData {
    @Schema(description = "金智达柱状图数据结构")
    private List<CommonStaVO> barChartData;
}
