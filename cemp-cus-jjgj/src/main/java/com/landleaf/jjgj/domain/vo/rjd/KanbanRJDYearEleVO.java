package com.landleaf.jjgj.domain.vo.rjd;

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
@Schema(name = "当年电耗分析VO", description = "当年电耗分析VO")
public class KanbanRJDYearEleVO {

	@Schema(description = "饼图数据")
	private List<CommonStaVO> pieChartData;

	@Schema(description = "柱状图数据")
	private List<CommonStaVO> barChartData;
}
