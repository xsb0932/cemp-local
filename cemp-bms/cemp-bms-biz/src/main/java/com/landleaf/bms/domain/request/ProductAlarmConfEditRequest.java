package com.landleaf.bms.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编辑产品告警配置
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Data
public class ProductAlarmConfEditRequest {
    /**
     * id
     */
    @NotNull(message = "id不能为空")
    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long productId;
    /**
     * 告警CODE
     */
    @NotNull(message = "告警CODE不能为空")
    @Schema(description = "告警CODE", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String alarmCode;
    /**
     * 告警描述
     */
    @Schema(description = "告警描述", example = "1")
    private String alarmDesc;
    /**
     * 告警触发等级 数据字典（ALARM_LEVEL）
     */
    @NotNull(message = "告警触发等级不能为空")
    @Schema(description = "告警触发等级 数据字典（ALARM_LEVEL）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String alarmTriggerLevel;
    /**
     * 告警复归等级 数据字典（ALARM_LEVEL）
     */
    @NotNull(message = "告警复归等级不能为空")
    @Schema(description = "告警复归等级 数据字典（ALARM_LEVEL）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String alarmRelapseLevel;
    /**
     * 告警确认方式 数据字典（ALARM_CONFIRM_TYPE）
     */
//    @NotNull(message = "告警确认方式不能为空")
    @Schema(description = "告警确认方式 数据字典（ALARM_CONFIRM_TYPE）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String alarmConfirmType;
}
