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
 * 统计表-设备指标-电表-统计年实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaDeviceElectricityYearEntity对象", description = "统计表-设备指标-电表-统计年")
@TableName("tb_project_sta_device_electricity_year")
public class ProjectStaDeviceElectricityYearEntity extends BaseEntity {

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
     * 有功用电量
     */
    @Schema(description = "有功用电量")
    @TableField("energymeter_epimport_total")
    private BigDecimal energymeterEpimportTotal;

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
     * 手工维护标识(0-系统 1手工)
     */
    @Schema(description = "手工维护标识(0-系统 1手工)")
    private Integer manualFlag;
}
