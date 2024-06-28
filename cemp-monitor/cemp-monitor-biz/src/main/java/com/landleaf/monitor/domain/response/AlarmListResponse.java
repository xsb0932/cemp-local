package com.landleaf.monitor.domain.response;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.landleaf.monitor.enums.AlarmObjTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 告警列表
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Data
@Schema(name = "告警列表", description = "告警列表")
public class AlarmListResponse {
    /**
     * id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 事件id
     */
    @Schema(description = "事件id")
    private String eventId;
    /**
     * 事件发生时间
     */
    @Schema(description = "事件发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTime;

    @Schema(description = "事件发生时间字符串表示")
    private String eventTimeStr;
    /**
     * 事件类型 数据字典（EVENT_TYPE）
     */
    @Schema(description = "事件类型")
    private String eventType;
    /**
     * 事件类型 名称
     */
    @Schema(description = "事件类型 名称")
    private String eventTypeName;
    /**
     * 告警对象类型 数据字典（ALARM_OBJ_TYPE）
     */
    @Schema(description = "告警对象类型 数据字典（ALARM_OBJ_TYPE）")
    private String alarmObjType;
    /**
     * 告警对象类型 名称
     */
    @Schema(description = "告警对象类型 名称")
    private String alarmObjTypeName;
    /**
     * 告警对象id
     */
    @Schema(description = "告警对象id")
    private String objId;
    /**
     * 告警对象名称
     */
    @Schema(description = "告警对象名称")
    private String objName;
    /**
     * 所属项目id
     */
    @Schema(description = "所属项目id")
    private String projectBizId;
    /**
     * 所属项目名称
     */
    @Schema(description = "所属项目名称")
    private String projectName;
    /**
     * 告警业务id
     */
    @Schema(description = "告警业务id")
    private String alarmBizId;
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
     * 告警类型名称
     */
    @Schema(description = "告警类型名称")
    private String alarmTypeName;
    /**
     * 告警等级 数据字典（ALARM_LEVEL）
     */
    @Schema(description = "告警等级 数据字典（ALARM_LEVEL）")
    private String alarmLevel;
    /**
     * 告警等级名称
     */
    @Schema(description = "告警等级名称")
    private String alarmLevelName;
    /**
     * 告警描述
     */
    @Schema(description = "告警描述")
    private String alarmDesc;
    /**
     * 告警状态 数据字典（ALARM_STATUS）
     */
    @Schema(description = "告警状态 数据字典（ALARM_STATUS）")
    private String alarmStatus;
    /**
     * 告警状态 名称
     */
    @Schema(description = "告警状态 名称")
    private String alarmStatusName;
    /**
     * 确认状态 是否确认
     */
    @Schema(description = "确认状态 是否确认")
    private Boolean isConfirm;
    /**
     * 确认人 自动确认事件，sys，其他-用户id
     */
    @Schema(description = "确认人")
    private String confirmUser;
    /**
     * 确认时间
     */

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;
    /**
     * 确认备注
     */
    @Schema(description = "确认备注")
    private String confirmRemark;

    @Schema(description = "是否有下一条：0=>无;1=>有")
    private Integer hasNext;

    public String getObjName() {
        if (AlarmObjTypeEnum.PROJECT.getCode().equals(alarmObjType)) {
            return projectName;
        }
        return objName;
    }

    public String getEventTimeStr() {
        return DateUtil.format(confirmTime, "yyyy年MM月dd日 HH:mm");
    }
}
