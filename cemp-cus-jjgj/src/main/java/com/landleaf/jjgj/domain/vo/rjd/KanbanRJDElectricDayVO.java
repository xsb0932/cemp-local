package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * KanbanRJDElectricDayVO 锦江看板数据-日用电曲线
 *
 * @author xshibai
 * @since 2023-08-04
 */
@Data
@Schema(name = "KanbanRJDElectricDayVO对象", description = "KanbanRJDElectricDayVO对象")
public class KanbanRJDElectricDayVO {


	@Schema(description = "本周数据")
	private KanbanRJDElectricDayDetailVO today;

	@Schema(description = "上周数据")
	private KanbanRJDElectricDayDetailVO yestoday;

}
