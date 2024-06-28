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
 * @since 2023-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaSubitemDayEntity对象", description = "ProjectStaSubitemDayEntity对象")
@TableName("tb_project_sta_subitem_day")
public class ProjectStaSubitemDayEntity extends BaseEntity {

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
     * 总用气费
     */
    @Schema(description = "总用气费")
    @TableField("project_gas_fee_total")
    private BigDecimal projectGasFeeTotal;

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
     * 总计用水费
     */
    @Schema(description = "总计用水费")
    @TableField("project_water_fee_water")
    private BigDecimal projectWaterFeeWater;

    /**
     * 总计污水处理费
     */
    @Schema(description = "总计污水处理费")
    @TableField("project_water_fee_sewerage")
    private BigDecimal projectWaterFeeSewerage;

    /**
     * 总计水费
     */
    @Schema(description = "总计水费")
    @TableField("project_water_fee_total")
    private BigDecimal projectWaterFeeTotal;

    /**
     * 全部负荷平用电量
     */
    @Schema(description = "全部负荷平用电量")
    @TableField("project_electricity_energyusage_flat")
    private BigDecimal projectElectricityEnergyusageFlat;

    /**
     * 全部负荷总用电量
     */
    @Schema(description = "全部负荷总用电量")
    @TableField("project_electricity_energyusage_total")
    private BigDecimal projectElectricityEnergyusageTotal;

    /**
     * 全部负荷电度电费
     */
    @Schema(description = "全部负荷电度电费")
    @TableField("project_electricity_energyusagefee_total")
    private BigDecimal projectElectricityEnergyusagefeeTotal;

    /**
     * 全部负荷尖用电量
     */
    @Schema(description = "全部负荷尖用电量")
    @TableField("project_electricity_energyusage_tip")
    private BigDecimal projectElectricityEnergyusageTip;

    /**
     * 全部负荷谷用电量
     */
    @Schema(description = "全部负荷谷用电量")
    @TableField("project_electricity_energyusage_valley")
    private BigDecimal projectElectricityEnergyusageValley;

    /**
     * 全部负荷峰用电量
     */
    @Schema(description = "全部负荷峰用电量")
    @TableField("project_electricity_energyusage_peak")
    private BigDecimal projectElectricityEnergyusagePeak;

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
    @TableField("project_electricity_subHeatingWaterEnergy_total")
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
     * 全部能耗标准煤当量
     */
    @Schema(description = "全部能耗标准煤当量")
    @TableField("project_carbon_totalcoal_total")
    private BigDecimal projectCarbonTotalcoalTotal;

    /**
     * 全部能耗粉尘当量
     */
    @Schema(description = "全部能耗粉尘当量")
    @TableField("project_carbon_totaldust_total")
    private BigDecimal projectCarbonTotaldustTotal;

    /**
     * 全部能耗CO2当量
     */
    @Schema(description = "全部能耗CO2当量")
    @TableField("project_carbon_totalco2_total")
    private BigDecimal projectCarbonTotalco2Total;

    /**
     * 全部能耗SO2当量
     */
    @Schema(description = "全部能耗SO2当量")
    @TableField("project_carbon_totalso2_total")
    private BigDecimal projectCarbonTotalso2Total;

    /**
     * 气耗CO2当量
     */
    @Schema(description = "气耗CO2当量")
    @TableField("project_carbon_gasusageco2_total")
    private BigDecimal projectCarbonGasusageco2Total;

    /**
     * 气耗标准煤当量
     */
    @Schema(description = "气耗标准煤当量")
    @TableField("project_carbon_gasusagecoal_total")
    private BigDecimal projectCarbonGasusagecoalTotal;

    /**
     * 气耗粉尘当量
     */
    @Schema(description = "气耗粉尘当量")
    @TableField("project_carbon_gasusagedust_total")
    private BigDecimal projectCarbonGasusagedustTotal;

    /**
     * 气耗SO2当量
     */
    @Schema(description = "气耗SO2当量")
    @TableField("project_carbon_gasusageso2_total")
    private BigDecimal projectCarbonGasusageso2Total;

    /**
     * 电耗SO2当量
     */
    @Schema(description = "电耗SO2当量")
    @TableField("project_carbon_electricityusageso2_total")
    private BigDecimal projectCarbonElectricityusageso2Total;

    /**
     * 电耗粉尘当量
     */
    @Schema(description = "电耗粉尘当量")
    @TableField("project_carbon_electricityusagedust_total")
    private BigDecimal projectCarbonElectricityusagedustTotal;

    /**
     * 电耗CO2当量
     */
    @Schema(description = "电耗CO2当量")
    @TableField("project_carbon_electricityusageco2_total")
    private BigDecimal projectCarbonElectricityusageco2Total;

    /**
     * 电耗标准煤当量
     */
    @Schema(description = "电耗标准煤当量")
    @TableField("project_carbon_electricityusagecoal_total")
    private BigDecimal projectCarbonElectricityusagecoalTotal;

    /**
     * 水耗粉尘当量
     */
    @Schema(description = "水耗粉尘当量")
    @TableField("project_carbon_waterusagedust_total")
    private BigDecimal projectCarbonWaterusagedustTotal;

    /**
     * 水耗标煤当量
     */
    @Schema(description = "水耗标煤当量")
    @TableField("project_carbon_waterusagecoal_total")
    private BigDecimal projectCarbonWaterusagecoalTotal;

    /**
     * 水耗CO2当量
     */
    @Schema(description = "水耗CO2当量")
    @TableField("project_carbon_waterusageco2_total")
    private BigDecimal projectCarbonWaterusageco2Total;

    /**
     * 水耗SO2当量
     */
    @Schema(description = "水耗SO2当量")
    @TableField("project_carbon_waterusageso2_total")
    private BigDecimal projectCarbonWaterusageso2Total;


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
     * 购网电费(由购网尖峰谷平电量及电价得到。)
     * project.electricity.pccEnergyUsageFee.total
     */
    @TableField("project_electricity_pccenergyusagefee_total")
    private BigDecimal projectElectricityPccEnergyUsageFeeTotal;

    /**
     * 购网尖电量(根据尖时段配置，由小时表汇总得到。。)
     * project.electricity.pccEnergyUsage.tip
     */
    @TableField("project_electricity_pccenergyusage_tip")
    private BigDecimal projectElectricityPccEnergyUsageTip;

    /**
     * 购网峰电量(根据峰时段配置，由小时表汇总得到。)
     * project.electricity.pccEnergyUsage.peak
     */
    @TableField("project_electricity_pccenergyusage_peak")
    private BigDecimal projectElectricityPccEnergyUsagePeak;

    /**
     * 购网谷电量(根据谷时段配置，由小时表汇总得到。)
     * project.electricity.pccEnergyUsage.valley
     */
    @TableField("project_electricity_pccenergyusage_valley")
    private BigDecimal projectElectricityPccEnergyUsageValley;

    /**
     * 购网平电量(根据平时段配置，由小时表汇总得到)
     * project.electricity.pccEnergyUsage.flat
     */
    @TableField("project_electricity_pccenergyusage_flat")
    private BigDecimal projectElectricityPccEnergyUsageFlat;

    /**
     * 上网电量(由小时表汇总得到)
     * project.electricity.pccEnergyProduction.total
     */
    @TableField("project_electricity_pccenergyproduction_total")
    private BigDecimal projectElectricityPccEnergyProductionTotal;

    /**
     * 上网收益(由上网电量*上网电价)
     * project.electricity.pccEnergyProductionFee.total
     */
    @TableField("project_electricity_pccenergyproductionfee_total")
    private BigDecimal projectElectricityPccEnergyProductionFeeTotal;

    /**
     * 光伏发电量(由小时表汇总得到)
     * project.electricity.pvEnergyProduction.total
     */
    @TableField("project_electricity_pvenergyproduction_total")
    private BigDecimal projectElectricityPvEnergyProductionTotal;

    /**
     * 上网电量(同关口上网电量指标)
     * project.electricity.pvEnergyProductionGrid.total
     */
    @TableField("project_electricity_pvenergyproductiongrid_total")
    private BigDecimal projectElectricityPvEnergyProductionGridTotal;

    /**
     * 直接使用(光伏总发电-上网-先储后用)
     * project.electricity.pvEnergyProductionLoad.total
     */
    @TableField("project_electricity_pvenergyproductionload_total")
    private BigDecimal projectElectricityPvEnergyProductionLoadTotal;

    /**
     * 先储后用(由储光电量汇总)
     * project.electricity.pvEnergyProductionStorage.total
     */
    @TableField("project_electricity_pvenergyproductionstorage_total")
    private BigDecimal projectElectricityPvEnergyProductionStorageTotal;

    /**
     * 光伏收益(上网收益+消纳)
     * project.electricity.pvEnergyProductionFee.total
     */
    @TableField("project_electricity_pvenergyproductionfee_total")
    private BigDecimal projectElectricityPvEnergyProductionFeeTotal;

    /**
     * 上网收益（同关口上网收益）
     * project.electricity.pvEnergyProductionGridFee.total
     */
    @TableField("project_electricity_pvenergyproductiongridfee_total")
    private BigDecimal projectElectricityPvEnergyProductionGridFeeTotal;

    /**
     * 消纳收益(sum(（小时发电量-小时上网电量）* 当时单价))
     * project.electricity.pvEnergyProductionUsageFee.total
     */
    @TableField("project_electricity_pvenergyproductionusagefee_total")
    private BigDecimal projectElectricityPvEnergyProductionUsageFeeTotal;

    /**
     * 储充电量(由小时表汇总得到)
     * project.electricity.storageEnergyUsage.total
     */
    @TableField("project_electricity_storageenergyusage_total")
    private BigDecimal projectElectricityStorageEnergyUsageTotal;

    /**
     * 尖充电量(根据尖时段配置，由小时表汇总得到。)
     * project.electricity.storageEnergyUsage.tip
     */
    @TableField("project_electricity_storageenergyusage_tip")
    private BigDecimal projectElectricityStorageEnergyUsageTip;

    /**
     * 峰充电量(根据峰时段配置，由小时表汇总得到)
     * project.electricity.storageEnergyUsage.peak
     */
    @TableField("project_electricity_storageenergyusage_peak")
    private BigDecimal projectElectricityStorageEnergyUsagePeak;

    /**
     * 谷充电量(根据谷时段配置，由小时表汇总得到)
     * project.electricity.storageEnergyUsage.valley
     */
    @TableField("project_electricity_storageenergyusage_valley")
    private BigDecimal projectElectricityStorageEnergyUsageValley;

    /**
     * 平充电量（根据平时段配置，由小时表汇总得到）
     * project.electricity.storageEnergyUsage.flat
     */
    @TableField("project_electricity_storageenergyusage_flat")
    private BigDecimal projectElectricityStorageEnergyUsageFlat;

    /**
     * 储光电量（由小时表汇总得到）
     * project.electricity.storageEnergyUsagePv.total
     */
    @TableField("project_electricity_storageenergyusagepv_total")
    private BigDecimal projectElectricityStorageEnergyUsagePvTotal;

    /**
     * 储市电量（由小时表汇总得到）
     * project.electricity.storageEnergyUsageGrid.total
     */
    @TableField("project_electricity_storageenergyusagegrid_total")
    private BigDecimal projectElectricityStorageEnergyUsageGridTotal;

    /**
     * 储放电量（由小时表汇总得到）
     * project.electricity.storageEnergyProduction.total
     */
    @TableField("project_electricity_storageenergyproduction_total")
    private BigDecimal projectElectricityStorageEnergyProductionTotal;

    /**
     * 尖放电量(根据尖时段配置，由小时表汇总得到。)
     * project.electricity.storageEnergyProduction.tip
     */
    @TableField("project_electricity_storageenergyproduction_tip")
    private BigDecimal projectElectricityStorageEnergyProductionTip;

    /**
     * 峰放电量(根据峰时段配置，由小时表汇总得到)
     * project.electricity.storageEnergyProduction.peak
     */
    @TableField("project_electricity_storageenergyproduction_peak")
    private BigDecimal projectElectricityStorageEnergyProductionPeak;

    /**
     * 谷放电量(根据谷时段配置，由小时表汇总得到)
     * project.electricity.storageEnergyProduction.valley
     */
    @TableField("project_electricity_storageenergyproduction_valley")
    private BigDecimal projectElectricityStorageEnergyProductionValley;

    /**
     * 平放电量(根据平时段配置，由小时表汇总得到)
     * project.electricity.storageEnergyProduction.flat
     */
    @TableField("project_electricity_storageenergyproduction_flat")
    private BigDecimal projectElectricityStorageEnergyProductionFlat;

    /**
     * 储能净收益(sum(放电时段单价*放电时段电量）-sum(充电时段单价*充电时段电量))
     * project.electricity.storageEnergyNetFee.total
     */
    @TableField("project_electricity_storageenergynetfee_total")
    private BigDecimal projectElectricityStorageEnergyNetFeeTotal;

    /**
     * 充电桩总用电(由小时表汇总得到)
     * project.electricity.subChargeEnergy.total
     */
    @TableField("project_electricity_subchargeenergy_total")
    private BigDecimal projectElectricitySubChargeEnergyTotal;

    /**
     * 充电总费用(电度费+服务费；计费模式及服务费配置到数据库中。)
     * project.electricity.subChargeEnergyFee.total
     */
    @TableField("project_electricity_subchargeenergyfee_total")
    private BigDecimal projectElectricitySubChargeEnergyFeeTotal;

    /**
     * 电度费(由购网尖峰谷平电量及电价得到)
     * project.electricity.subChargeEnergyChargeFee.total
     */
    @TableField("project_electricity_subchargeenergychargefee_total")
    private BigDecimal projectElectricitySubChargeEnergyChargeFeeTotal;

    /**
     * 服务费(总电量*服务费单价)
     * project.electricity.subChargeEnergyServiceFee.total
     */
    @TableField("project_electricity_subchargeenergyservicefee_total")
    private BigDecimal projectElectricitySubChargeEnergyServiceFeeTotal;

    /**
     * 照明用电电量
     * project.electricity.subLightingEnergy.total
     */
    @TableField("project_electricity_sublightingenergy_total")
    private BigDecimal projectElectricitySubLightingEnergyTotal;

    /**
     * 插座用电电量
     * project.electricity.subSocketEnergy.total
     */
    @TableField("project_electricity_subsocketenergy_total")
    private BigDecimal projectElectricitySubSocketEnergyTotal;

    /**
     * 标准煤(CO2减排量=光伏发电量 * CO2排放因子
     * 排放因子同用电的排放因子。
     * 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
     * project.carbon.pvReductionCoal.total
     */
    @TableField("project_carbon_pvreductioncoal_total")
    private BigDecimal projectCarbonPvReductionCoalTotal;

    /**
     * CO2(CO2减排量=光伏发电量 * CO2排放因子
     * 排放因子同用电的排放因子。
     * 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
     * project.carbon.pvReductionCO2.total
     */
    @TableField("project_carbon_pvreductionco2_total")
    private BigDecimal projectCarbonPvReductionCO2Total;

    /**
     * SO2(CO2减排量=光伏发电量 * CO2排放因子
     * 排放因子同用电的排放因子。
     * 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
     * project.carbon.pvReductionSO2.total
     */
    @TableField("project_carbon_pvreductionso2_total")
    private BigDecimal projectCarbonPvReductionSO2Total;

    /**
     * 粉尘(CO2减排量=光伏发电量 * CO2排放因子
     * 排放因子同用电的排放因子。
     * 标准煤、SO2、粉尘根据CO2*系数得到。系数同之前相同。)
     * project.carbon.pvReductionDust.total
     */
    @TableField("project_carbon_pvreductiondust_total")
    private BigDecimal projectCarbonPvReductionDustTotal;

    /**
     * 全部负荷平电费
     */
    @Schema(description = "全部负荷平电费")
    @TableField("project_electricity_energyusagefee_flat")
    private BigDecimal projectElectricityEnergyusagefeeFlat;
    /**
     * 全部负荷尖电费
     */
    @Schema(description = "全部负荷尖电费")
    @TableField("project_electricity_energyusagefee_tip")
    private BigDecimal projectElectricityEnergyusagefeeTip;

    /**
     * 全部负荷谷电费
     */
    @Schema(description = "全部负荷谷电费")
    @TableField("project_electricity_energyusagefee_valley")
    private BigDecimal projectElectricityEnergyusagefeeValley;

    /**
     * 全部负荷峰电费
     */
    @Schema(description = "全部负荷峰电费")
    @TableField("project_electricity_energyusagefee_peak")
    private BigDecimal projectElectricityEnergyusagefeePeak;

    /**
     * 空调补水
     * project.water.HVACUsage.total
     */
    @Schema(description = "空调补水")
    @TableField("project_water_hvacusage_total")
    private BigDecimal projectWaterHvacusageTotal;

    /**
     * 热水补水
     * project.water.HeatingWaterUsage.total
     */
    @Schema(description = "热水补水")
    @TableField("project_water_heatingwaterusage_total")
    private BigDecimal projectWaterHeatingwaterusageTotal;

    /**
     * 空调单方用电量
     * project.electricity.subHAVCEnergy.avgSq
     */
    @Schema(description = "空调单方用电量")
    @TableField("project_electricity_subhavcenergy_avgsq")
    private BigDecimal projectElectricitySubhavcenergyAvgsq;

    /**
     * 热水单立方用电量
     * project.electricity.subHeatingWaterEnergy.avgCube
     */
    @Schema(description = "热水单立方用电量")
    @TableField("project_electricity_subheatingwaterenergy_avgcube")
    private BigDecimal projectElectricitySubheatingwaterenergyAvgcube;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;
}
