package com.landleaf.comm.sta.enums;

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
    TOTAL_ELECTRICITY_CONSUMPTION_OF_ALL_LOADS("project_electricity_energyusage_total", "project_electricity_energyusage_total")
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
