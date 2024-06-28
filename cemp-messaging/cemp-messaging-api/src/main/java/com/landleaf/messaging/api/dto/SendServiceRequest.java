package com.landleaf.messaging.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SendServiceRequest {

    @Schema(description = "bizDeviceId")
    private String bizDeviceId;

    @Schema(description = "bizProjId")
    private String bizProjId;

    private String bizProdId;

    @Schema(description = "外部设备id")
    private String sourceDeviceId;

    @Schema(description = "时间戳")
    private Long time;

    /**
     * 功能标识符
     */
    @Schema(description = "功能标识符")
    private String identifier;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    /**
     * 事件参数
     * 可由多个参数构成。每个参数，由字段标识符、名称、数据类型、单位、值描述组成、。参数字段的标识符只需要在参数内做标识符唯一校验。
     */
    @Schema(description = "事件参数")
    private List<FunctionParameter> serviceParameter;
}
