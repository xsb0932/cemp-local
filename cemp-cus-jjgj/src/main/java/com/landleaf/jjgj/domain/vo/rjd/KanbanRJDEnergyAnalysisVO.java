package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * KanbanRJDEnergyAnalysisVO 锦江看板数据-年用能分析
 *
 * @author xshibai
 * @since 2023-09-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "KanbanRJDEnergyAnalysisVO对象", description = "KanbanRJDEnergyAnalysisVO对象")
public class KanbanRJDEnergyAnalysisVO {

	@Schema(description = "描述")
	private String name;
	@Schema(description = "实际值")
	private BigDecimal energyUseage;
	@Schema(description = "计划值")
	private BigDecimal energyPlan;
	@Schema(description = "同比")
	private BigDecimal yoy;

}
