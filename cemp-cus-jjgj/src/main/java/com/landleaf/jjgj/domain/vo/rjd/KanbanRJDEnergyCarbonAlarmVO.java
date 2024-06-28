package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * KanbanRJDEnergyCarbonAlarmVO 锦江看板数据-当年碳排
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDEnergyCarbonAlarmVO对象", description = "KanbanRJDEnergyCarbonAlarmVO对象")
public class KanbanRJDEnergyCarbonAlarmVO {

	@Schema(description = "实际排放")
	private BigDecimal totalCO2;
	@Schema(description = "计划排放")
	private BigDecimal planCO2;
	@Schema(description = "超额预警0:正常1:超额预警2:排放超额")
	private Integer alarmStatus;

}
