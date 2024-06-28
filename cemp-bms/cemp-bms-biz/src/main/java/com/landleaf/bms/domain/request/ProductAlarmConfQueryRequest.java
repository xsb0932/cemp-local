package com.landleaf.bms.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 查询产品告警配置
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Data
public class ProductAlarmConfQueryRequest extends PageParam {
    /**
     * 产品id
     */
    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long productId;
    /**
     * 告警CODE
     */
    @Schema(description = "告警CODE", example = "1")
    private String alarmCode;
    /**
     * 告警触发等级 数据字典（ALARM_LEVEL）
     */
    @Schema(description = "告警触发等级 数据字典（ALARM_LEVEL）", example = "1")
    private String alarmTriggerLevel;

}
