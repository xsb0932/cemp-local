package com.landleaf.energy.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * ProjectKpiVO 指标和维度
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "ProjectKpiConfigVO对象", description = "ProjectKpiConfigVO对象")
public class ProjectKpiVO {


	/**
	 * 指标维度名称
	 */
		@Schema(description = "指标维度名称")
		private String name;

	/**
	 * 指标维度明细
	 */
	@Schema(description = "指标维度名称")
	private List<ProjectKpiVODetail> details;
}
