package com.landleaf.job.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.sql.Timestamp;


/**
 * 定时任务日志实体类
 *
 * @author hebin
 * @since 2023-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "ScheduleJobLoggerEntity", description = "定时任务日志")
@TableName("tb_schedule_job_logger")
public class ScheduleJobLoggerEntity extends BaseEntity {

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 定时任务id
     */
    @Schema(description = "定时任务id")
    private Long jobId;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;

    /**
     * 项目id列表
     */
    @Schema(description = "项目id列表")
    private String projectIds;

    /**
     * 项目名称列表
     */
    @Schema(description = "项目名称列表")
    private String projectNames;

    /**
     * 运行状态（0成功 1失败）
     */
    @Schema(description = "运行状态（0成功 1失败）")
    private Integer status;

    /**
     * 运行状态（0自动 1人工）
     */
    @Schema(description = "运行状态（0自动 1人工）")
    private Integer execType;

    /**
     * 执行时间
     */
    @Schema(description = "执行时间")
    private Timestamp execTime;

    /**
     * 执行人（0-sys 其他-用户id）
     */
    @Schema(description = "执行人（0-sys 其他-用户id）")
    private Long execUser;
}