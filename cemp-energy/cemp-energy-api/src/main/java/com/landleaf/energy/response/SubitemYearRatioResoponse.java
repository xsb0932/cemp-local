package com.landleaf.energy.response;

import com.landleaf.comm.vo.CommonStaVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * KanbanRJDYearEleVO 锦江看板-当年电耗分析VO
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "负荷结构", description = "负荷结构")
public class SubitemYearRatioResoponse {

	@Schema(description = "当年负荷使用结构")
	private List<CommonStaVO> pieChartData;
	@Schema(description = "当年负荷机构月趋势")
	private List<CommonStaVO> barChartData;

}
