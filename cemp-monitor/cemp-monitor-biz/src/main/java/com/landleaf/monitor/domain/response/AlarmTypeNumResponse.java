package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 告警类型数量
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Data
@Schema(name = "告警类型数量", description = "告警类型数量")
public class AlarmTypeNumResponse {
    /**
     * 告警类型 数据字典（ALARM_TYPE）
     */
    @Schema(description = "告警类型 数据字典（ALARM_TYPE）")
    private String alarmType;
    /**
     * 告警类型 名称
     */
    @Schema(description = "告警类型 名称")
    private String alarmTypeName;
    /**
     * 告警数量
     */
    @Schema(description = "告警数量")
    private Integer number;

    /**
     * 未读告警数量
     */
    @Schema(description = "未读告警数量")
    private Integer unreadNumber;

    /**
     * 已读告警数量
     */
    @Schema(description = "已读告警数量")
    private Integer readNumber;
}
