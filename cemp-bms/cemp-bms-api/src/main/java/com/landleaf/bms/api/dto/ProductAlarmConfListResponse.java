package com.landleaf.bms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 产品告警配置
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Data
public class ProductAlarmConfListResponse {
    /**
     * id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 产品id
     */
    @Schema(description = "产品id")
    private Long productId;
    /**
     * 告警CODE
     */
    @Schema(description = "告警CODE")
    private String alarmCode;
    /**
     * 告警描述
     */
    @Schema(description = "告警描述")
    private String alarmDesc;
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
     * 告警触发等级 数据字典（ALARM_LEVEL）
     */
    @Schema(description = "告警触发等级 数据字典（ALARM_LEVEL）")
    private String alarmTriggerLevel;
    /**
     * 告警触发等级 名称
     */
    @Schema(description = "告警触发等级 名称")
    private String alarmTriggerLevelName;
    /**
     * 告警复归等级 数据字典（ALARM_LEVEL）
     */
    @Schema(description = "告警复归等级 数据字典（ALARM_LEVEL）")
    private String alarmRelapseLevel;
    /**
     * 告警复归等级 名称
     */
    @Schema(description = "告警复归等级 名称")
    private String alarmRelapseLevelName;
    /**
     * 告警确认方式 数据字典（ALARM_CONFIRM_TYPE）
     */
    @Schema(description = "告警确认方式 数据字典（ALARM_CONFIRM_TYPE）")
    private String alarmConfirmType;
    /**
     * 告警确认方式 名称
     */
    @Schema(description = "告警确认方式 名称")
    private String alarmConfirmTypeName;

    @Schema(description = "复归确认等级")
    private String alarmRelapseConfirmType;
}
