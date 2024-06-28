package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 电耗数据-分区RO
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "KanbanRJDEleYearRO对象", description = "KanbanRJDEleYearRO对象")
public class KanbanRJDEleYearRO {


	@Schema(description = "分区名称")
	private String name;
	@Schema(description = "分区代码")
	private String subareaCode;
	@Schema(description = "统计值")
	private BigDecimal svalue;


}
