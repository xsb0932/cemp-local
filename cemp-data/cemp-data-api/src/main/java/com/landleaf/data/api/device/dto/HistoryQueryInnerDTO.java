package com.landleaf.data.api.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "HistoryQueryInnerDTO对象", description = "设备查询历史状态的内部参数封装")
public class HistoryQueryInnerDTO {
    /**
     * 业务的产品编号
     */
    @Schema(name = "业务的产品编号")
    private String bizProductId;

    @Schema(name = "开始时间yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @Schema(name = "截至时间yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @Schema(name = "设备业务编号，多个以逗号分割")
    private String bizDeviceIds;

    @Schema(name = "属性编码，多个以逗号分割")
    private String attrCode;

    @Schema(name = "周期类型：0=>原始（默认）、1=>5分钟、2=>10分钟、3=>30分钟、4=>1小时、5=>8小时、6=>1天")
    private Integer periodType;
}