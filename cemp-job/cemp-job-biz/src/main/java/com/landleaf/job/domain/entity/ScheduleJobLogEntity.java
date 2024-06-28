/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.landleaf.job.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.landleaf.pgsql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "tb_schedule_job_log")
public class ScheduleJobLogEntity extends BaseEntity {

    /**
     * 日志id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务id
     */
    private Long jobId;

    /**
     * 接口地址
     */
    private String apiUrl;

    /**
     * 任务状态（0成功 1失败）
     */
    private Integer status;

    /**
     * 日志信息
     */
    private String logInfo;

    /**
     * 失败信息
     */
    private String error;

    /**
     * 耗时(单位：毫秒)
     */
    private Integer times;

}
