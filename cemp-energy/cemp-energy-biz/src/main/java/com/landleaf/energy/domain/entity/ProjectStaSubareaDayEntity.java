package com.landleaf.energy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 实体类
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ProjectStaSubareaDayEntity对象", description = "ProjectStaSubareaDayEntity对象")
@TableName("tb_project_sta_subarea_day")
public class ProjectStaSubareaDayEntity extends BaseEntity {

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标CODE
     */
    @Schema(description = "指标CODE")
    private String kpiCode;

    /**
     * 分区代码
     */
    @Schema(description = "分区代码")
    private String subareaCode;

    /**
     * 分区名字
     */
    @Schema(description = "分区名字")
    private String subareaName;

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
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

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
     * 统计-天
     */
    @Schema(description = "统计-天")
    private String day;

    /**
     * 统计值
     */
    @Schema(description = "统计值")
    private BigDecimal staValue;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private Timestamp staTime;
}
