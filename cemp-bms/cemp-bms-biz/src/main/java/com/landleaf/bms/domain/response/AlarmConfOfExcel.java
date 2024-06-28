package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备批量导入解析对象
 *
 * @author hebin
 * @since 2023-07-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AlarmConfOfExcel对象", description = "设备批量导入解析对象")
public class AlarmConfOfExcel {
    /**
     * 告警CODE 当前产品下唯一
     */
    private String alarmCode;
    /**
     * 告警类型 数据字典（ALARM_TYPE）默认添加设备告警
     */
    private String alarmType;
    /**
     * 告警描述
     */
    private String alarmDesc;
    /**
     * 告警触发等级 数据字典（ALARM_LEVEL）
     */
    private String alarmTriggerLevel;
    /**
     * 告警复归等级 数据字典（ALARM_LEVEL）
     */
    private String alarmRelapseLevel;
    /**
     * 告警确认方式 数据字典（ALARM_CONFIRM_TYPE）
     */
    private String alarmConfirmType;

}
