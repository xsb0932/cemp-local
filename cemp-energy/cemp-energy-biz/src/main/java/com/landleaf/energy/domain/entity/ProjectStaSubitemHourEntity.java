package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * 实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaSubitemHourEntity对象", description = "ProjectStaSubitemHourEntity对象")
@TableName("tb_project_sta_subitem_hour")
public class ProjectStaSubitemHourEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 项目CODE
     */
    @Schema(description = "项目CODE")
    private String projectCode;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 租户CODE
     */
    @Schema(description = "租户CODE")
    private String tenantCode;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 统计-年
     */
    @Schema(description = "统计-年")
    private String year;

    /**
     * 统计-月
     */
    @Schema(description = "统计-月")
    private String month;

    /**
     * 统计-天
     */
    @Schema(description = "统计-天")
    private String day;

    /**
     * 统计小时
     */
    @Schema(description = "统计小时")
    private String hour;

    /**
     * 总用气量
     */
    @Schema(description = "总用气量")
    @TableField("project_gas_usage_total")
    private BigDecimal projectGasUsageTotal;

    /**
     * 总用水量
     */
    @Schema(description = "总用水量")
    @TableField("project_water_usage_total")
    private BigDecimal projectWaterUsageTotal;

    /**
     * 全部负荷总用电量
     */
    @Schema(description = "全部负荷总用电量")
    @TableField("project_electricity_energyusage_total")
    private BigDecimal projectElectricityEnergyusageTotal;

    /**
     * 电梯总用电量
     */
    @Schema(description = "电梯总用电量")
    @TableField("project_electricity_subelevatorenergy_total")
    private BigDecimal projectElectricitySubelevatorenergyTotal;

    /**
     * 客房总用电量
     */
    @Schema(description = "客房总用电量")
    @TableField("project_electricity_subguestroomenergy_total")
    private BigDecimal projectElectricitySubguestroomenergyTotal;

    /**
     * 热水总用电量
     */
    @Schema(description = "热水总用电量")
    @TableField("project_electricity_subheatingwaterenergy_total")
    private BigDecimal projectElectricitySubheatingwaterenergyTotal;

    /**
     * 空调总用电量
     */
    @Schema(description = "空调总用电量")
    @TableField("project_electricity_subhavcenergy_total")
    private BigDecimal projectElectricitySubhavcenergyTotal;

    /**
     * 动力用电量
     */
    @Schema(description = "动力用电量")
    @TableField("project_electricity_subPowerSupplyEnergy_total")
    private BigDecimal projectElectricitySubpowersupplyenergyTotal;

    /**
     * 其他总用电量
     */
    @Schema(description = "其他总用电量")
    @TableField("project_electricity_subothertype_total")
    private BigDecimal projectElectricitySubothertypeTotal;

    /**
     * 供水总用电量
     */
    @Schema(description = "供水总用电量")
    @TableField("project_electricity_subwatersupplyenergy_total")
    private BigDecimal projectElectricitySubwatersupplyenergyTotal;

    /**
     * 平均温度
     */
    @TableField("project_environment_outtemp_avg")
    private BigDecimal projectEnvironmentOutTempAvg;

    /**
     * 最大温度
     */
    @TableField("project_environment_outtemp_max")
    private BigDecimal projectEnvironmentOutTempMax;

    /**
     * 最小温度
     */
    @TableField("project_environment_outtemp_min")
    private BigDecimal projectEnvironmentOutTempMin;

    /**
     * 平均湿度
     */
    @TableField("project_environment_outtumidity_avg")
    private BigDecimal projectEnvironmentOutTumidityAvg;

    /**
     * 购网电量
     * project.electricity.pccEnergyUsage.total
     */
    @TableField("project_electricity_pccenergyusage_total")
    private BigDecimal projectElectricityPccEnergyUsageTotal;

    /**
     * 上网电量
     * project.electricity.pccEnergyProduction.total
     */
    @TableField("project_electricity_pccenergyproduction_total")
    private BigDecimal projectElectricityPccEnergyProductionTotal;

    /**
     * 光伏总发电量
     * project.electricity.pvEnergyProduction.total
     */
    @TableField("project_electricity_pvenergyproduction_total")
    private BigDecimal projectElectricityPvEnergyProductionTotal;

    /**
     * 储充电量
     * project.electricity.storageEnergyUsage.total
     */
    @TableField("project_electricity_storageenergyusage_total")
    private BigDecimal projectElectricityStorageEnergyUsageTotal;

    /**
     * 储光电量
     * project.electricity.storageEnergyUsagePv.total
     */
    @TableField("project_electricity_storageenergyusagepv_total")
    private BigDecimal projectElectricityStorageEnergyUsagePvTotal;

    /**
     * 储市电量
     * project.electricity.storageEnergyUsageGrid.total
     */
    @TableField("project_electricity_storageenergyusagegrid_total")
    private BigDecimal projectElectricityStorageEnergyUsageGridTotal;

    /**
     * 储放电量
     * project.electricity.storageEnergyProduction.total
     */
    @TableField("project_electricity_storageenergyproduction_total")
    private BigDecimal projectElectricityStorageEnergyProductionTotal;

    /**
     * 照明总用电量
     * project.electricity.subLightingEnergy.total
     */
    @TableField("project_electricity_sublightingenergy_total")
    private BigDecimal projectElectricitySubLightingEnergyTotal;

    /**
     * 总充电桩电量
     * project.electricity.subChargeEnergy.total
     */
    @TableField("project_electricity_subchargeenergy_total")
    private BigDecimal projectElectricitySubChargeEnergyTotal;

    /**
     * 插座总用电量
     * project.electricity.subSocketEnergy.total
     */
    @TableField("project_electricity_subsocketenergy_total")
    private BigDecimal projectElectricitySubSocketEnergyTotal;


    /**
     * 空调补水
     * project.water.HVACUsage.total
     */
    @TableField("project_water_hvacusage_total")
    private BigDecimal projectWaterHvacusageTotal;

    /**
     * 热水补水
     * project.water.HeatingWaterUsage.total
     */
    @TableField("project_water_heatingwaterusage_total")
    private BigDecimal projectWaterHeatingwaterusageTotal;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;
}
