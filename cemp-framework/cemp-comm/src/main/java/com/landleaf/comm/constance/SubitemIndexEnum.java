package com.landleaf.comm.constance;

import lombok.Getter;

/**
 * 分项指标枚举
 *
 * @author yue lin
 * @since 2023/7/28 11:22
 */
@Getter
public enum SubitemIndexEnum {

    /**
     * 电梯用电量
     * project_electricity_subelevatorenergy_total
     */
    ENERGY_SUBELEVATOR_TOTAL("projectElectricitySubelevatorenergyTotal", ""),
    /**
     * 客房总用电量
     * project_electricity_subguestroomenergy_total
     */
    ENERGY_GUESTROOM_TOTAL("projectElectricitySubguestroomenergyTotal", ""),
    /**
     * 热水总用电量
     * project_electricity_subHeatingWaterEnergy_total
     */
    ENERGY_HEATWATER_TOTAL("projectElectricitySubheatingwaterenergyTotal", ""),
    /**
     * 供水总用电
     * project.electricity.subWaterSupplyEnergy.total
     */
    ENERGY_SUBWATER_SUPPLY_TOTAL("projectElectricitySubwatersupplyenergyTotal", ""),
    /**
     * 空调总用电量
     * project_electricity_subelevatorenergy_total
     */
    ENERGY_SUBHVAC_TOTAL("projectElectricitySubhavcenergyTotal", ""),
    /**
     * 动力总用电量
     * project_electricity_subPowerSupplyEnergy_total
     */
    ENERGY_SUBPOWER_TOTAL("projectElectricitySubpowersupplyenergyTotal", ""),


    /**
     * 气耗CO2当量
     * project.carbon.gasusageco2.total
     */
    CARBON_GAS_USAGE_CO2_TOTAL("projectCarbonGasusageco2Total", ""),

    /**
     * 全部CO2当量
     * project.carbon.totalco2.total
     */
    CARBON_USAGE_CO2_TOTAL("projectCarbonTotalco2Total", ""),

    /**
     * 电耗CO2当量
     * project.carbon.electricityusageco2.total
     */
    CARBON_ELECTRICITY_USAGE_CO2_TOTAL("projectCarbonElectricityusageco2Total", ""),

    /**
     * 水耗CO2当量
     * project.carbon.waterusagedust.total
     */
    CARBON_WATER_USAGE_CO2_TOTAL("projectCarbonWaterusagedustTotal", ""),

    /**
     * 用水量
     * project_gas_usage_total
     */
    WATER_USAGE_TOTAL("projectWaterUsageTotal", ""),
    /**
     * 用气量
     * project_water_usage_total
     */
    GAS_USAGE_TOTAL("projectGasUsageTotal", ""),
    /**
     * 总电费
     * project_electricity_energyusagefee_total
     */
    ELECTRICITY_ENERGY_USAGE_FEE_TOTAL("projectElectricityEnergyusagefeeTotal", ""),
    /**
     * 峰
     * project.electricity.energyUsageFee.peak
     */
    ELECTRICITY_ENERGY_USAGE_FEE_PEAK("projectElectricityEnergyusagefeePeak", ""),

    /**
     * 谷
     * project.electricity.energyUsageFee.valley
     */
    ELECTRICITY_ENERGY_USAGE_FEE_VALLEY("projectElectricityEnergyusagefeeValley", ""),

    /**
     * 平
     * project.electricity.energyUsageFee.flat
     */
    ELECTRICITY_ENERGY_USAGE_FEE_FLAT("projectElectricityEnergyusagefeeFlat", ""),

    /**
     * 尖
     * project.electricity.energyUsageFee.tip
     */
    ELECTRICITY_ENERGY_USAGE_FEE_TIP("projectElectricityEnergyusagefeeTip", ""),

    /**
     * 水费
     * project.water.fee.total
     */
    WATER_FEE_TOTAL("projectWaterFeeTotal", ""),

    /**
     * 气费
     * project.gas.fee.total
     */
    GAS_FEE_TOTAL("projectGasFeeTotal", ""),


    /**
     * 购网电量/市电用电量
     * project.electricity.pccEnergyUsage.total
     */
    PURCHASE_NETWORK_ELECTRICITY("projectElectricityPccEnergyUsageTotal", "projectElectricityPccEnergyUsageTotal"),
    /**
     * 上网电量
     * project.electricity.pccEnergyProduction.total
     */
    ON_GRID_ENERGY("projectElectricityPccEnergyProductionTotal", "projectElectricityPccEnergyProductionTotal"),
    /**
     * 光伏上网电量
     * project.electricity.pvEnergyProductionGrid.total
     */
    PV_ON_GRID_ENERGY("projectElectricityPvEnergyProductionGridTotal", "projectElectricityPccEnergyProductionTotal"),
    /**
     * 光伏直接使用
     * project.electricity.pvEnergyProductionLoad.total
     */
    PV_DIRECT_USE("projectElectricityPvEnergyProductionLoadTotal", ""),
    /**
     * 光伏发电量
     * project.electricity.pvEnergyProduction.total
     */
    PHOTOVOLTAIC_POWER_GENERATION("projectElectricityPvEnergyProductionTotal", "projectElectricityPvEnergyProductionTotal"),
    /**
     * 储能放电量
     * project.electricity.storageEnergyProduction.total
     */
    ENERGY_STORAGE_AND_DISCHARGE_CAPACITY("projectElectricityStorageEnergyProductionTotal", "projectElectricityStorageEnergyProductionTotal"),
    /**
     * 充电桩充电量
     * project.electricity.subChargeEnergy.total
     */
    ELECTRIC_QUANTITY_OF_CHARGING_STATION("projectElectricitySubChargeEnergyTotal", "projectElectricitySubChargeEnergyTotal"),
    /**
     * 二氧化碳排放量
     * project.carbon.totalCO2.total
     */
    CARBON_DIOXIDE_EMISSIONS("projectCarbonTotalco2Total", ""),
    /**
     * 标准煤
     * project.carbon.gasusagecoal.total
     */
    CARBON_USAGE_COAL_TOTAL("projectCarbonGasusagecoalTotal", ""),
    /**
     * 二氧化碳减少量
     * project.carbon.pvReductionCO2.total
     */
    CARBON_DIOXIDE_REDUCTION("projectCarbonPvReductionCO2Total", ""),
    /**
     * 光伏收益
     * project.electricity.pvEnergyProductionFee.total
     */
    PHOTOVOLTAIC_REVENUE("projectElectricityPvEnergyProductionFeeTotal", ""),
    /**
     * 储能收益
     * project.electricity.storageEnergyNetFee.total
     */
    ENERGY_STORAGE_REVENUE("projectElectricityStorageEnergyNetFeeTotal", ""),
    /**
     * 充电桩收益
     * project.electricity.subChargeEnergyServiceFee.total
     */
    INCOME_OF_CHARGING_STATION("projectElectricitySubChargeEnergyServiceFeeTotal", ""),
    /**
     * 暖通用电量(空调)
     * project.electricity.subHAVCEnergy.total
     */
    WARM_UNIVERSAL_POWER("projectElectricitySubhavcenergyTotal", "projectElectricitySubhavcenergyTotal"),
    /**
     * 照明用电量
     * project.electricity.subLightingEnergy.total
     */
    LIGHTING_POWER_CONSUMPTION("projectElectricitySubLightingEnergyTotal", "projectElectricitySubLightingEnergyTotal"),
    /**
     * 插座用电量
     * project.electricity.subSocketEnergy.total
     */
    SOCKET_POWER_CONSUMPTION("projectElectricitySubSocketEnergyTotal", "projectElectricitySubSocketEnergyTotal"),
    /**
     * 动力用电量
     * project_electricity_subPowerSupplyEnergy_total
     */
    SUPPLY_ENERTY("projectElectricitySubpowersupplyenergyTotal", "projectElectricitySubpowersupplyenergyTotal"),
    /**
     * 其他用电量
     * project_electricity_subOtherType_total
     */
    OTHER_ELECTRICITY_CONSUMPTION("projectElectricitySubothertypeTotal", "projectElectricitySubothertypeTotal"),

    /**
     * 直接使用
     * project.electricity.pvEnergyProductionLoad.total
     */
    DIRECT_USE("projectElectricityPvEnergyProductionLoadTotal", ""),
    /**
     * 储光电量(先储后用)
     * project.electricity.pvEnergyProductionStorage.total
     */
    PV_STORE_BEFORE_USE("projectElectricityPvEnergyProductionStorageTotal", "projectElectricityStorageEnergyUsagePvTotal"),
    /**
     * 储充电量
     * project.electricity.storageEnergyUsage.total
     */
    STORAGE_AND_CHARGING_CAPACITY("projectElectricityStorageEnergyUsageTotal", "projectElectricityStorageEnergyUsageTotal"),
    /**
     * 储市电量
     * project.electricity.storageEnergyUsageGrid.total
     */
    STORAGE_OF_ELECTRICITY_IN_THE_CITY("projectElectricityStorageEnergyUsageGridTotal", ""),
    /**
     * 储光电量
     * project.electricity.storageEnergyUsagePv.total
     */
    STORAGE_CAPACITY("projectElectricityStorageEnergyUsagePvTotal", ""),
    /**
     * 储能放电量(尖)
     * project.electricity.storageEnergyProduction.tip
     */
    ENERGY_STORAGE_AND_DISCHARGE_CAPACITY_TIP("projectElectricityStorageEnergyProductionTip", ""),
    /**
     * 储能放电量(峰)
     * project.electricity.storageEnergyProduction.peak
     */
    ENERGY_STORAGE_AND_DISCHARGE_CAPACITY_PEAK("projectElectricityStorageEnergyProductionPeak", ""),

    /**
     * 全部负荷总用电量
     * project_electricity_energyusage_total
     */
    TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS("projectElectricityEnergyusageTotal", "projectElectricityEnergyusageTotal")
    ;


    /**
     * 年月日数据库字段
     */
    private final String field;

    /**
     * 小时数据库字段
     */
    @Getter
    private final String hourFiled;

    SubitemIndexEnum(String field, String hourFiled) {
        this.field = field;
        this.hourFiled = hourFiled;
    }
}
