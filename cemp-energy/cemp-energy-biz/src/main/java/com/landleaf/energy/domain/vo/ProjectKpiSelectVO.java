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
@Schema(name = "ProjectKpiSelectVO对象", description = "ProjectKpiSelectVO对象")
public class ProjectKpiSelectVO {


	/**
	 * 指标大类
	 */
		@Schema(description = "指标大类:1电 2水 3气 4碳")
		private String kpiType;

	/**
	 * 指标分类代码
	 */
	@Schema(description = "指标分类代码")
	private String kpiSubtype;

	/**
	 * 指标分类名称
	 */
	@Schema(description = "指标分类代码")
	private String name;

	/**
	 * 分项分区标志
	 */
	@Schema(description = "分项分区标志 1:分项 2:分区")
	private String tag;

	@Schema(description = "指标集")
	private List<EnergySelectedVO> kpis;


}
