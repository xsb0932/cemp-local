package com.landleaf.job.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务
 *
 * @author Yang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_schedule_job")
public class ScheduleJobEntity extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务调度参数key
     */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    /**
     * 任务id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 服务名（任务组名）
     */
    private String serviceName;

    /**
     * 接口地址
     */
    private String apiUrl;

    /**
     * cron表达式
     */
    private String cronExpression;

    /**
     * 任务状态（0正常 1暂停）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户id(默认0 为所有租户)
     */
    @Schema(description = "租户id(默认0 为所有租户)")
    private Long tenantId;

    /**
     * 统计类型（0时 1日 2月 3年）
     */
    @Schema(description = "统计类型（0时 1日 2月 3年）")
    private Integer statisticType;

    /**
     * 上次运行状态（0成功 1失败)
     */
    @Schema(description = "上次运行状态（0成功 1失败)")
    private Integer lastStatus;

    /**
     * 上次运行时间
     */
    @Schema(description = "上次运行时间")
    private LocalDateTime lastTime;
}
