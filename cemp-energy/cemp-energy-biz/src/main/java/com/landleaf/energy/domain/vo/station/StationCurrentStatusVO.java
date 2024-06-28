package com.landleaf.energy.domain.vo.station;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * StationCurrentStatusVO 站点系统图-设备当前状态值
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "StationCurrentStatusVO对象", description = "StationCurrentStatusVO对象")
public class StationCurrentStatusVO {

	@Schema(description = "通讯状态")
	private String comStatus;
	@Schema(description = "控制模式")
	private String mod;
	@Schema(description = "运行状态")
	private String runStatus;
	@Schema(description = "空调设定")
	private String tmpSet;
	@Schema(description = "当前温度")
	private String tempNow;
	@Schema(description = "当前功率")
	private String power;
	@Schema(description = "当日电量")
	private String electricityQuantity;


}
