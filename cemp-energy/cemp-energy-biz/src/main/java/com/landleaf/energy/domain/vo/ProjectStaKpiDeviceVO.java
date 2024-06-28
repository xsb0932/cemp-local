package com.landleaf.energy.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "设备统计VO", description = "设备统计VO")
public class ProjectStaKpiDeviceVO extends ProjectStaKpiVO {

    @Schema(description = "电表设备-有功用电量")
    private BigDecimal energymeterEpimportTotal;
    /**
     * 有功发电量
     */
    @Schema(description = "有功发电量")
    private BigDecimal energymeterEpexportTotal;

    @Schema(description = "水表设备-用水量")
    private BigDecimal watermeterUsageTotal;

    @Schema(description = "气类设备-用气量")
    private BigDecimal gasmeterUsageTotal;

    @Schema(description = "空调在线时长")
    private BigDecimal airconditionercontrollerOnlinetimeTotal;

    @Schema(description = "空调运行时长")
    private BigDecimal airconditionercontrollerRunningtimeTotal;

    @Schema(description = "空调平均温度")
    private BigDecimal airconditionercontrollerActualtempAvg;

    /**
     * 在线时长
     */
    @Schema(description = "在线时长")
    private BigDecimal znbOnlineTimeTotal;

    /**
     * 发电时长
     */
    @Schema(description = "发电时长")
    private BigDecimal znbRunningTimeTotal;

    /**
     * 有功发电量
     */
    @Schema(description = "有功发电量")
    private BigDecimal znbEpexportTotal;

    /**
     * 等效发电小时
     */
    @Schema(description = "等效发电小时")
    private BigDecimal znbEptoHourTotal;

    /**
     * 有功用电量
     */
    @Schema(description = "有功用电量")
    private BigDecimal znbEpimportTotal;

    /**
     * 无功发电量
     */
    @Schema(description = "无功发电量")
    private BigDecimal znbEqexportTotal;

    /**
     * 无功用电量
     */
    @Schema(description = "无功用电量")
    private BigDecimal znbEqimportTotal;

    /**
     * 最大发电功率
     */
    @Schema(description = "最大发电功率")
    private BigDecimal znbPMax;

    /**
     * 在线时长
     */
    @Schema(description = "在线时长")
    private BigDecimal gscnOnlineTimeTotal;

    /**
     * 充电时长
     */
    @Schema(description = "充电时长")
    private BigDecimal gscnChargeTimeTotal;

    /**
     * 放电时长
     */
    @Schema(description = "放电时长")
    private BigDecimal gscnDischargeTimeTotal;

    /**
     * 待机时长
     */
    @Schema(description = "待机时长")
    private BigDecimal gscnStandbyTimeTotal;

    /**
     * 充电电量
     */
    @Schema(description = "充电电量")
    private BigDecimal gscnEpimportTotal;

    /**
     * 放电电量
     */
    @Schema(description = "放电电量")
    private BigDecimal gscnEpexportTotal;

}
