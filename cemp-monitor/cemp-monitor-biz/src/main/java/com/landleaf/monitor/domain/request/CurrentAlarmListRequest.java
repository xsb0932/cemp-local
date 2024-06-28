package com.landleaf.monitor.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 告警列表请求参数
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Data
@Schema(name = "告警列表请求参数", description = "告警列表请求参数")
public class CurrentAlarmListRequest extends PageParam {
    /**
     * 项目业务ids
     */
    @Schema(description = "项目业务ids,逗号分割")
    @NotNull(message = "项目业务ids不能为空")
    private String projectBizIds;
    /**
     * 告警对象名称
     */
    @Schema(description = "告警对象名称")
    private String objName;
    /**
     * 告警码
     */
    @Schema(description = "告警码")
    private String alarmCode;
    /**
     * 告警类型 数据字典（ALARM_TYPE）
     */
    @Schema(description = "告警类型 数据字典（ALARM_TYPE）")
    private String alarmType;
    /**
     * 告警等级 数据字典（ALARM_LEVEL）
     */
    @Schema(description = "告警等级 数据字典（ALARM_LEVEL）")
    private String alarmLevel;
    /**
     * 确认状态 是否确认
     */
    @Schema(description = "确认状态 是否确认")
    private Boolean isConfirm;

}
