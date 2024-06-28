package com.landleaf.energy.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * KanbanRJDEnergyWeeksVO 锦江看板数据-7日能耗-明细数据
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDEnergyWeeksDetailVO对象", description = "KanbanRJDEnergyWeeksDetailVO对象")
public class KanbanRJDEnergyWeeksDetailVO {

	@Schema(description = "值列表")
	List<String> attrs;

	@Schema(description = "x轴")
	List<String> xlist;
}
