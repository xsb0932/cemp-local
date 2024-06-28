package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * KanbanRJDElectricityPlanVO 锦江看板数据-用电计划
 *
 * @author xshibai
 * @since 2023-09-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "KanbanRJDElectricityPlanVO对象", description = "KanbanRJDElectricityPlanVO对象")
public class KanbanRJDElectricityPlanVO {

	@Schema(description = "描述")
	private String name;
	@Schema(description = "用电量")
	private BigDecimal energyUseage;
	@Schema(description = "计划值")
	private BigDecimal energyPlan;
	@Schema(description = "剩余值")
	private BigDecimal energyLeft;
	@Schema(description = "进度")
	private BigDecimal energyRate;

}
