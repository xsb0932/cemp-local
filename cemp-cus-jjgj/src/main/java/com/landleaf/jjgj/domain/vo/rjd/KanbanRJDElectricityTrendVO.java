package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * KanbanRJDElectricityTrendVO 锦江看板数据-用电趋势
 *
 * @author xshibai
 * @since 2023-09-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "KanbanRJDElectricityTrendVO对象", description = "KanbanRJDElectricityTrendVO对象")
public class KanbanRJDElectricityTrendVO {

	@Schema(description = "描述")
	private String name;
	@Schema(description = "用电量")
	private BigDecimal energyUseage;
	@Schema(description = "计划值")
	private BigDecimal energyPlan;
	@Schema(description = "入住率")
	private BigDecimal checkinRate;

}
