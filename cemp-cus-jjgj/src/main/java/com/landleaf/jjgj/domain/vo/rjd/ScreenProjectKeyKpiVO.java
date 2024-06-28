package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ScreenProjectBasicVO 锦江定制大屏-关键指标
 *
 * @author xshibai
 * @since 2023-11-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "锦江定制大屏-关键指标VO", description = "锦江定制大屏-关键指标VO")
public class ScreenProjectKeyKpiVO {

	@Schema(description = "电耗使用量")
	private BigDecimal eleUsage;

	@Schema(description = "电耗当年占比")
	private BigDecimal eleUsageRatio;

	@Schema(description = "水耗使用量")
	private BigDecimal waterUsage;

	@Schema(description = "水耗当年占比")
	private BigDecimal waterUsageRatio;

	@Schema(description = "气耗使用量")
	private BigDecimal gasUsage;

	@Schema(description = "气耗当年占比")
	private BigDecimal gasUsageRatio;

	@Schema(description = "碳排用量")
	private BigDecimal carbonUsage;

	@Schema(description = "碳排当年占比")
	private BigDecimal carbonUsageRatio;
}
