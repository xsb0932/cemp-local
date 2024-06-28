package com.landleaf.jjgj.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * KanbanRJDAlarmVO 锦江定制预警
 *
 * @author xshibai
 * @since 2023-09-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "KanbanRJDAlarmVO对象", description = "KanbanRJDAlarmVO对象")
public class KanbanRJDAlarmVO {

	@Schema(description = "时间")
	private String time;
	@Schema(description = "预警类型")
	private String alarmType;
	@Schema(description = "预警内容")
	private String alarmContent;
	@Schema(description = "状态")
	private String alarmStatus;


}
