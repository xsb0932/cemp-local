package com.landleaf.monitor.domain.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 历史事件列表
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Data
@Schema(name = "历史事件列表", description = "历史事件列表")
public class HistoryEventListRequest extends PageParam {
    /**
     * 项目业务ids
     */
    @Schema(description = "项目业务ids")
    @NotNull(message = "项目业务ids不能为空")
    private List<String> projectBizIds;
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
     * 告警状态 数据字典（ALARM_STATUS）
     */
    @Schema(description = "告警状态 数据字典（ALARM_STATUS）")
    private String alarmStatus;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "是否确认：0=>否，1=>是")
    private Boolean isConfirm;

    public void setIsConfirm(Boolean isConfirm) {
        this.isConfirm = isConfirm;
    }
}
