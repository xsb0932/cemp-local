package com.landleaf.monitor.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "HistoryQueryDTO对象", description = "设备查询历史状态的参数封装")
public class HistoryQueryDTO {

    @Schema(name="times", description = "开始时间，结束时间yyyy-MM-dd HH:mm:ss")
    private String[] times;

    @Schema(name="bizDeviceIds", description="设备业务编号，多个以逗号分割")
    private String bizDeviceIds;

    @Schema(name= "attrCode" , description="属性编码，多个以逗号分割")
    private String attrCode;

    @Schema(name= "periodType" , description="周期类型：0=>原始（默认）、1=>5分钟、2=>10分钟、3=>30分钟、4=>1小时、5=>8小时、6=>1天")
    private Integer periodType;
}