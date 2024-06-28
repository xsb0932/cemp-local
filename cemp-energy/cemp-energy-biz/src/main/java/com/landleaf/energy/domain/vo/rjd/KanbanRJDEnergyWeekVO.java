package com.landleaf.energy.domain.vo.rjd;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * KanbanRJDEnegyVO 锦江看板数据-7日能耗
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@Schema(name = "KanbanRJDEnergyWeekVO对象", description = "KanbanRJDEnergyWeekVO对象")
public class KanbanRJDEnergyWeekVO {


	@Schema(description = "周一用电")
	private BigDecimal electricityMondy;
	@Schema(description = "周二用电")
	private BigDecimal electricityTuesday;
	@Schema(description = "周三用电")
	private BigDecimal electricityWednesday;
	@Schema(description = "周四用电")
	private BigDecimal electricityThursday;
	@Schema(description = "周五用电")
	private BigDecimal electricityFriday;
	@Schema(description = "周六用电")
	private BigDecimal electricitySaturday;
	@Schema(description = "周日用电")
	private BigDecimal electricitySunday;

	@Schema(description = "上周一用电")
	private BigDecimal lastweekElectricityMondy;
	@Schema(description = "上周二用电")
	private BigDecimal lastweekElectricityTuesday;
	@Schema(description = "上周三用电")
	private BigDecimal lastweekElectricityWednesday;
	@Schema(description = "上周四用电")
	private BigDecimal lastweekElectricityThursday;
	@Schema(description = "上周五用电")
	private BigDecimal lastweekElectricityFriday;
	@Schema(description = "上周六用电")
	private BigDecimal lastweekElectricitySaturday;
	@Schema(description = "上周日用电")
	private BigDecimal lastweekElectricitySunday;

	@Schema(description = "周一用水")
	private BigDecimal waterMondy;
	@Schema(description = "周二用水")
	private BigDecimal waterTuesday;
	@Schema(description = "周三用水")
	private BigDecimal waterWednesday;
	@Schema(description = "周四用水")
	private BigDecimal waterThursday;
	@Schema(description = "周五用水")
	private BigDecimal waterFriday;
	@Schema(description = "周六用水")
	private BigDecimal waterSaturday;
	@Schema(description = "周日用水")
	private BigDecimal waterSunday;

	@Schema(description = "上周一用水")
	private BigDecimal lastweekWaterMondy;
	@Schema(description = "上周二用水")
	private BigDecimal lastweekWaterTuesday;
	@Schema(description = "上周三用水")
	private BigDecimal lastweekWaterWednesday;
	@Schema(description = "上周四用水")
	private BigDecimal lastweekWaterThursday;
	@Schema(description = "上周五用水")
	private BigDecimal lastweekWaterFriday;
	@Schema(description = "上周六用水")
	private BigDecimal lastweekWaterSaturday;
	@Schema(description = "上周日用水")
	private BigDecimal lastweekWaterSunday;

	@Schema(description = "周一用气")
	private BigDecimal gasMondy;
	@Schema(description = "周二用气")
	private BigDecimal gasTuesday;
	@Schema(description = "周三用气")
	private BigDecimal gasWednesday;
	@Schema(description = "周四用气")
	private BigDecimal gasThursday;
	@Schema(description = "周五用气")
	private BigDecimal gasFriday;
	@Schema(description = "周六用气")
	private BigDecimal gasSaturday;
	@Schema(description = "周日用气")
	private BigDecimal gasSunday;

	@Schema(description = "上周一用气")
	private BigDecimal lastweekGasMondy;
	@Schema(description = "上周二用气")
	private BigDecimal lastweekGasTuesday;
	@Schema(description = "上周三用气")
	private BigDecimal lastweekgasWednesday;
	@Schema(description = "上周四用气")
	private BigDecimal lastweekGasThursday;
	@Schema(description = "上周五用气")
	private BigDecimal lastweekGasFriday;
	@Schema(description = "上周六用气")
	private BigDecimal lastweekGasSaturday;
	@Schema(description = "上周日用气")
	private BigDecimal lastweekGasSunday;

	public KanbanRJDEnergyWeekVO() {
		this.electricityMondy = new BigDecimal(0);
		this.electricityTuesday = new BigDecimal(0);
		this.electricityWednesday = new BigDecimal(0);
		this.electricityThursday = new BigDecimal(0);
		this.electricityFriday = new BigDecimal(120);
		this.electricitySaturday = new BigDecimal(0);
		this.electricitySunday = new BigDecimal(0);
		this.lastweekElectricityMondy = new BigDecimal(0);
		this.lastweekElectricityTuesday = new BigDecimal(0);
		this.lastweekElectricityWednesday = new BigDecimal(0);
		this.lastweekElectricityThursday = new BigDecimal(0);
		this.lastweekElectricityFriday = new BigDecimal(0);
		this.lastweekElectricitySaturday = new BigDecimal(0);
		this.lastweekElectricitySunday = new BigDecimal(0);
		this.waterMondy = new BigDecimal(0);
		this.waterTuesday = new BigDecimal(0);
		this.waterWednesday = new BigDecimal(0);
		this.waterThursday = new BigDecimal(0);
		this.waterFriday = new BigDecimal(80);
		this.waterSaturday = new BigDecimal(0);
		this.waterSunday = new BigDecimal(0);
		this.lastweekWaterMondy = new BigDecimal(0);
		this.lastweekWaterTuesday = new BigDecimal(0);
		this.lastweekWaterWednesday = new BigDecimal(0);
		this.lastweekWaterThursday = new BigDecimal(0);
		this.lastweekWaterFriday = new BigDecimal(0);
		this.lastweekWaterSaturday = new BigDecimal(0);
		this.lastweekWaterSunday = new BigDecimal(0);
		this.gasMondy = new BigDecimal(0);
		this.gasTuesday = new BigDecimal(0);
		this.gasWednesday = new BigDecimal(0);
		this.gasThursday = new BigDecimal(0);
		this.gasFriday = new BigDecimal(0);
		this.gasSaturday = new BigDecimal(0);
		this.gasSunday = new BigDecimal(0);
		this.lastweekGasMondy = new BigDecimal(0);
		this.lastweekGasTuesday = new BigDecimal(0);
		this.lastweekgasWednesday = new BigDecimal(0);
		this.lastweekGasThursday = new BigDecimal(0);
		this.lastweekGasFriday = new BigDecimal(0);
		this.lastweekGasSaturday = new BigDecimal(0);
		this.lastweekGasSunday = new BigDecimal(0);
	}
}
