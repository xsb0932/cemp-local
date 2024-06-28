package com.landleaf.jjgj.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * ProjectKpiVO 指标和维度
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProjectKpiConfigVO对象", description = "ProjectKpiConfigVO对象")
public class ProjectKpiVODetail {

		@Schema(description = "指标code")
		private String code;

	/**
	 * 指标维度名称
	 */
	@Schema(description = "指标维度名称")
	private String name;







}
