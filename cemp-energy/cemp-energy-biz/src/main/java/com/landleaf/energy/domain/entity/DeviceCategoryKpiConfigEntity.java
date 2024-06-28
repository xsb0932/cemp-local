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
 * 实体类
 *
 * @author hebin
 * @since 2023-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "DeviceCategoryKpiConfigEntity对象", description = "DeviceCategoryKpiConfigEntity对象")
@TableName("tb_device_category_kpi_config")
public class DeviceCategoryKpiConfigEntity extends BaseEntity {

    /**
     * 指标ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标代码
     */
    private String code;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 品类id
     */
    private String bizCategoryId;

    /**
     * 品类名称
     */
    private String categoryName;

    /**
     * 分项大类代码
     */
    private String kpiTypeCode;

    /**
     * 分项大类 水 气 电 ...
     */
    private String kpiType;

    /**
     * 统计间隔-小时-1:是 0 否
     */
    private Integer staIntervalHour;

    /**
     * 统计间隔-日月年-1:是 0 否
     */
    private Integer staIntervalYmd;

    /**
     * 单位
     */
    private String unit;
}
