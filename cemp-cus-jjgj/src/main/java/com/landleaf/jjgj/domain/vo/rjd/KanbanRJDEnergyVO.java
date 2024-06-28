package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * KanbanRJDEnegyVO 锦江看板项目VO
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDEnegyVO对象", description = "KanbanRJDEnegyVO对象")
public class KanbanRJDEnergyVO {


	@Schema(description = "当年用电")
	private BigDecimal electricityYear;
	@Schema(description = "当月用电")
	private BigDecimal electricityMonth;
	@Schema(description = "用电环比")
	private BigDecimal electricityQOQ;
	@Schema(description = "用电同比")
	private BigDecimal electricityYOY;

	@Schema(description = "当年用气")
	private BigDecimal gasYear;
	@Schema(description = "当月用气")
	private BigDecimal gasMonth;
	@Schema(description = "用气环比")
	private BigDecimal gasQOQ;
	@Schema(description = "用气同比")
	private BigDecimal gasYOY;

	@Schema(description = "当年用水")
	private BigDecimal waterYear;
	@Schema(description = "当月用水")
	private BigDecimal waterMonth;
	@Schema(description = "用水环比")
	private BigDecimal waterQOQ;
	@Schema(description = "用水同比")
	private BigDecimal waterYOY;

	@Schema(description = "当年碳排")
	private BigDecimal carbonYear;
	@Schema(description = "当月碳排")
	private BigDecimal carbonMonth;
	@Schema(description = "碳排环比")
	private BigDecimal carbonQOQ;
	@Schema(description = "碳排同比")
	private BigDecimal carbonYOY;

	@Schema(description = "当年费用")
	private BigDecimal costYear;
	@Schema(description = "当月费用")
	private BigDecimal costMonth;
	@Schema(description = "费用环比")
	private BigDecimal costQOQ;
	@Schema(description = "费用同比")
	private BigDecimal costYOY;
}
