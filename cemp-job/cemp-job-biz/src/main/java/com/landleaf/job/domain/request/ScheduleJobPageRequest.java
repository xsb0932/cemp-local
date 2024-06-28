package com.landleaf.job.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务列表查询参数
 *
 * @author Yang
 */
@Data
@Schema(description = "任务列表查询参数封装")
public class ScheduleJobPageRequest extends PageParam {
    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "上次运行状态（0成功 1失败)")
    private Integer lastStatus;

    @Schema(description = "统计类型（0时 1日 2月 3年）")
    private Integer statisticType;
}
