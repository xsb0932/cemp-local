package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 指标库表实体类
 *
 * @author hebin
 * @since 2023-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectKpiConfigEntity对象", description = "指标库表")
@TableName("tb_project_kpi_config")
public class ProjectKpiConfigEntity extends BaseEntity {

    /**
     * 指标ID
     */
    @Schema(description = "指标ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标code
     */
    @Schema(description = "指标code")
    private String code;

    /**
     * 指标名称
     */
    @Schema(description = "指标名称")
    private String name;

    /**
     * 分项大类 水 气 电 ...
     */
    @Schema(description = "分项大类 水 气 电 ...")
    private String kpiType;

    /**
     * 分项类型代码
     */
    @Schema(description = "分项类型代码")
    private String kpiSubtype;

    /**
     * 统计间隔-小时-1:是 0 否
     */
    @Schema(description = "统计间隔-小时-1:是 0 否")
    private Integer staIntervalHour;

    /**
     * 统计间隔-日月年-1:是 0 否
     */
    @Schema(description = "统计间隔-日月年-1:是 0 否")
    private Integer staIntervalYmd;

    /**
     * 单位
     */
    @Schema(description = "单位")
    private String unit;

    /**
     * 分项大类代码
     */
    @Schema(description = "分项大类代码")
    private String kpiTypeCode;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;
}
