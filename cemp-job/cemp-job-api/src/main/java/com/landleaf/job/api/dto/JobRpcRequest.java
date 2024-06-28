package com.landleaf.job.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobRpcRequest {
    /**
     * 任务id
     */
    private Long jobId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 项目bizId集合
     */
    private List<String> projectList;

    /**
     * 执行用户id 0为sys
     */
    private Long execUser;

    /**
     * 执行时间 手动执行则传值
     */
    @Deprecated(since = "V3.9")
    private LocalDateTime execTime;

    /**
     * 手动开始时间
     * 时：yyyy-MM-dd HH:mm:ss
     * 日：yyyy-MM-dd
     * 月：yyyy-MM
     * 年：yyyy
     */
    private String startTime;

    /**
     * 手动结束时间
     * 时：yyyy-MM-dd HH:mm:ss
     * 日：yyyy-MM-dd
     * 月：yyyy-MM
     * 年：yyyy
     */
    private String endTime;

    /**
     * 执行类型 0-自动执行 1-人工执行
     */
    private Integer execType;
}
