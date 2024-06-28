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
 * 统计表-设备指标-组串逆变器-统计日实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaDeviceZnbDayEntity", description = "统计表-设备指标-组串逆变器-统计日")
@TableName("tb_project_sta_device_znb_day")
public class ProjectStaDeviceZnbDayEntity extends BaseEntity {

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
     * 在线时长
     */
    @Schema(description = "在线时长")
    @TableField("znb_online_time_total")
    private BigDecimal znbOnlineTimeTotal;

    /**
     * 发电时长
     */
    @Schema(description = "发电时长")
    @TableField("znb_running_time_total")
    private BigDecimal znbRunningTimeTotal;

    /**
     * 有功发电量
     */
    @Schema(description = "有功发电量")
    @TableField("znb_epexport_total")
    private BigDecimal znbEpexportTotal;

    /**
     * 等效发电小时
     */
    @Schema(description = "等效发电小时")
    @TableField("znb_eptohour_total")
    private BigDecimal znbEptoHourTotal;

    /**
     * 有功用电量
     */
    @Schema(description = "有功用电量")
    @TableField("znb_epimport_total")
    private BigDecimal znbEpimportTotal;

    /**
     * 无功发电量
     */
    @Schema(description = "无功发电量")
    @TableField("znb_eqexport_total")
    private BigDecimal znbEqexportTotal;

    /**
     * 无功用电量
     */
    @Schema(description = "无功用电量")
    @TableField("znb_eqimport_total")
    private BigDecimal znbEqimportTotal;

    /**
     * 最大发电功率
     */
    @Schema(description = "最大发电功率")
    @TableField("znb_p_max")
    private BigDecimal znbPMax;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;
}
