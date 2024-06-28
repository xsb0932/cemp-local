package com.landleaf.lh.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.TenantBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;


/**
 * 实体类
 *
 * @author hebin
 * @since 2024-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "MaintenanceSheetEntity", description = "MaintenanceSheetEntity对象")
@TableName("lh_maintenance_sheet")
public class MaintenanceSheetEntity extends TenantBaseEntity {

    /**
     * ID
     */
    @Schema(description = "ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目id（全局唯一id）
     */
    @Schema(description = "项目id（全局唯一id）")
    private String bizProjectId;

    /**
     * 报修年
     */
    @Schema(description = "报修年")
    private Integer maintenanceYear;

    /**
     * 报修月
     */
    @Schema(description = "报修月")
    private Integer maintenanceMonth;

    /**
     * 报修年月
     */
    @Schema(description = "报修年月")
    private LocalDate maintenanceYearMonth;

    /**
     * 报修日期(业主)
     */
    @Schema(description = "报修日期(业主)")
    private LocalDate maintenanceDate;

    /**
     * 报修单类别(详见枚举字典)
     */
    @Schema(description = "报修单类别(详见枚举字典)")
    private String maintenanceType;

    /**
     * 房号
     */
    @Schema(description = "房号")
    private String room;

    /**
     * 报修内容
     */
    @Schema(description = "报修内容")
    private String content;
}