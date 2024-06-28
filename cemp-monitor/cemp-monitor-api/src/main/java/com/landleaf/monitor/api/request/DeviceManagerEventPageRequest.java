package com.landleaf.monitor.api.request;

import com.landleaf.comm.base.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "设备管理-历史事件分页查询参数")
public class DeviceManagerEventPageRequest extends PageParam {
    @Schema(description = "设备业务id")
    @NotBlank(message = "设备业务id不能为空")
    private String bizDeviceId;

    @Schema(description = "告警类型 数据字典（ALARM_TYPE）")
    private String alarmType;

    @Schema(description = "开始时间", example = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @Schema(description = "结束时间", example = "yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

}
