package com.landleaf.monitor.domain.dto;

import lombok.Data;

/**
 * 项目未确认的告警的数量
 */
@Data
public class ProjUnconfirmedAlarmCountDTO {

    /**
     * bizProjId
     */
    private String bizProjId;

    /**
     * 未确认的告警数
     */
    private Integer count;
}
