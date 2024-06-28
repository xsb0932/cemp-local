package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * KanbanRJDEnergyCarbonVO 锦江看板数据-当年碳排
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDEnergyCarbonVO对象", description = "KanbanRJDEnergyCarbonVO对象")
public class KanbanRJDEnergyCarbonVO {

	@Schema(description = "标准煤")
	private BigDecimal totalCoal;
	@Schema(description = "二氧化碳")
	private BigDecimal totalCO2;
	@Schema(description = "二氧化硫")
	private BigDecimal totalSO2;
	@Schema(description = "粉尘")
	private BigDecimal totalDust;
	@Schema(description = "水碳排占比")
	private BigDecimal waterCO2Ratio;
	@Schema(description = "气碳排占比")
	private BigDecimal gasCO2Ratio;
	@Schema(description = "电碳排占比")
	private BigDecimal eleCO2Ratio;
}
