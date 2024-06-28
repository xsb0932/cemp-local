package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * KanbanRJDProjectVO 锦江看板项目VO
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDProjectVO对象", description = "KanbanRJDProjectVO对象")
public class KanbanRJDProjectVO {


	/**
	 * 项目面积
	 */
		@Schema(description = "项目面积")
		private String area;

	/**
	 * 客房数量
	 */
	@Schema(description = "客房数量")
	private Integer num;
}
