package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * KanbanRJDEnergyCarbonOrderVO 锦江看板数据-碳排排名
 *
 * @author xshibai
 * @since 2023-09-23
 */
@Data
@Schema(name = "KanbanRJDEnergyCarbonOrderVO对象", description = "KanbanRJDEnergyCarbonOrderVO对象")
public class KanbanRJDEnergyCarbonOrderVO {

	@Schema(description = "描述")
	private String name;
	@Schema(description = "当年排放")
	private BigDecimal totalCO2;
	@Schema(description = "计划排放")
	private BigDecimal yoy;

}
