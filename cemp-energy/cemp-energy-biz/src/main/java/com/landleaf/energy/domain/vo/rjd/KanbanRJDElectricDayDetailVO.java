package com.landleaf.energy.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * KanbanRJDElectricDayVO 锦江看板数据-日用电曲线
 *
 * @author xshibai
 * @since 2023-08-04
 */
@Data
@Schema(name = "KanbanRJDElectricDayDetailVO对象", description = "KanbanRJDElectricDayDetailVO对象")
public class KanbanRJDElectricDayDetailVO {


	@Schema(description = "y轴")
	List<String> ylist;

	@Schema(description = "x轴")
	List<String> xlist;

}
