package com.landleaf.job.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ScheduleJobBaseRequest {
    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String jobName;

    /**
     * 服务名（任务组名）
     */
    @Schema(description = "服务名（任务组名）")
    private String serviceName;

    /**
     * 接口地址
     */
    @Schema(description = "接口地址")
    private String apiUrl;

    /**
     * cron表达式
     */
    @Schema(description = "cron表达式")
    private String cronExpression;

    /**
     * 任务状态（0正常 1暂停）
     */
    @Schema(description = "任务状态（0正常 1暂停）")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
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
}
