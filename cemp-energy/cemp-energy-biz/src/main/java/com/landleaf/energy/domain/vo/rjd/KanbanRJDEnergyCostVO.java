package com.landleaf.energy.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * KanbanRJDEnergyCostVO 锦江看板数据-当年能耗成本VO
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDEnergyCostVO对象", description = "KanbanRJDEnergyCostVO对象")
public class KanbanRJDEnergyCostVO {

	@Schema(description = "电费")
	private BigDecimal energyUsageFee;
	@Schema(description = "尖电费")
	private BigDecimal energyUsageFeeTip;
	@Schema(description = "峰电费")
	private BigDecimal energyUsageFeePeek;
	@Schema(description = "谷电费")
	private BigDecimal energyUsageFeeValley;
	@Schema(description = "平电费")
	private BigDecimal energyUsageFeeFlat;
	@Schema(description = "水费")
	private BigDecimal waterFee;
	@Schema(description = "够水费")
	private BigDecimal waterFeeWater;
	@Schema(description = "污水费")
	private BigDecimal waterFeeSewerage;
	@Schema(description = "燃气费")
	private BigDecimal gasFee;
	@Schema(description = "总成本")
	private BigDecimal totalFee;
	@Schema(description = "同比")
	private BigDecimal yoy;
}
