package com.landleaf.jjgj.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * 实体类
 *
 * @author hebin
 * @since 2023-06-24
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectCnfTimePeriodEntity对象", description = "ProjectCnfTimePeriodEntity对象")
@TableName("tb_project_cnf_time_period")
public class ProjectCnfTimePeriodEntity extends BaseEntity {

    /**
     * 分时配置id
     */
    @Schema(description = "分时配置id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 时间段-年
     */
    @Schema(description = "时间段-年")
    private String periodYear;

    /**
     * 时间段-月
     */
    @Schema(description = "时间段-月")
    private String periodMonth;

    /**
     * 分时code
     */
    @Schema(description = "分时code")
    private String code;

    /**
     * 分时name
     */
    @Schema(description = "分时name")
    private String name;

    /**
     * 取值时间-开始
     */
    @Schema(description = "取值时间-开始")
    private Integer timeBegin;

    /**
     * 取值时间-结束
     */
    @Schema(description = "取值时间-结束")
    private Integer timeEnd;

    /**
     * 电价
     */
    @Schema(description = "电价")
    private BigDecimal price;

    /**
     * 租户ID
     */
    @Schema(description = " 租户ID")
    private Long tenantId;
}

