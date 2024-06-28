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
 * 统计表-设备指标-水表-统计天实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaDeviceWaterDayEntity对象", description = "统计表-设备指标-水表-统计天")
@TableName("tb_project_sta_device_water_day")
public class ProjectStaDeviceWaterDayEntity extends BaseEntity {

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
     * 统计-日
     */
    @Schema(description = "统计-日")
    private String day;

    /**
     * 用水量
     */
    @Schema(description = "用水量")
    @TableField("watermeter_usage_total")
    private BigDecimal watermeterUsageTotal;

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

    /**
     * 用水量期初值
     */
    @Schema(description = "用水量期初值")
    private BigDecimal watermeterUsageTotalStart;

    /**
     * 用水量期末值
     */
    @Schema(description = "用水量期末值")
    private BigDecimal watermeterUsageTotalEnd;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
