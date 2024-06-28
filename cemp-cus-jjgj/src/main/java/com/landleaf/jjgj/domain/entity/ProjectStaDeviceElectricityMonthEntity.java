package com.landleaf.jjgj.domain.entity;

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
 * 统计表-设备指标-电表-统计月实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaDeviceElectricityMonthEntity对象", description = "统计表-设备指标-电表-统计月")
@TableName("tb_project_sta_device_electricity_month")
public class ProjectStaDeviceElectricityMonthEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String bizDeviceId;

    /**
     * 产品ID
     */
    @Schema(description = "产品ID")
    private String bizProductId;

    /**
     * 品类ID
     */
    @Schema(description = "品类ID")
    private String bizCategoryId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String bizProjectId;

    /**
     * 项目代码
     */
    @Schema(description = "项目代码")
    private String projectCode;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 租户代码
     */
    @Schema(description = "租户代码")
    private String tenantCode;

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
     * 有功用电量
     */
    @Schema(description = "有功用电量")
    @TableField("energymeter_epimport_total")
    private BigDecimal energymeterEpimportTotal;

    /**
     * 有功用电量（期初）
     */
    @Schema(description = "有功用电量（期初）")
    @TableField("energymeter_epimport_start")
    private BigDecimal energymeterEpimportStart;

    /**
     * 有功用电量(期末)
     */
    @Schema(description = "有功用电量(期末)")
    @TableField("energymeter_epimport_end")
    private BigDecimal energymeterEpimportEnd;

    /**
     * 有功发电量
     */
    @Schema(description = "有功发电量")
    @TableField("energymeter_epexport_total")
    private BigDecimal energymeterEpexportTotal;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
