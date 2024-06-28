package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * KanbanRJDEnergyCarbonKeyKpiVO 锦江看板数据-碳关键指标
 *
 * @author xshibai
 * @since 2023-09-23
 */
@Data
@Schema(name = "KanbanRJDEnergyCarbonKeyKpiVO对象", description = "KanbanRJDEnergyCarbonKeyKpiVO对象")
public class KanbanRJDEnergyCarbonKeyKpiVO {

	@Schema(description = "标准煤")
	private BigDecimal totalCoal;
	@Schema(description = "碳排总量")
	private BigDecimal totalCO2;
	@Schema(description = "累计碳排总量")
	private BigDecimal totalCulCO2;
	@Schema(description = "当年碳排强度")
	private BigDecimal coalDensity;

}
