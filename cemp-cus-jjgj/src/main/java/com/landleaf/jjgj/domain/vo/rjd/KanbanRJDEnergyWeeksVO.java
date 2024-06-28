package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * KanbanRJDEnergyWeeksVO 锦江看板数据-7日能耗
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDEnergyWeeksVO对象", description = "KanbanRJDEnergyWeeksVO对象")
public class KanbanRJDEnergyWeeksVO {


	@Schema(description = "能耗类型")
	private String type;

	@Schema(description = "本周数据")
	private KanbanRJDEnergyWeeksDetailVO thisWeek;

	@Schema(description = "上周数据")
	private KanbanRJDEnergyWeeksDetailVO lastWeek;

}
