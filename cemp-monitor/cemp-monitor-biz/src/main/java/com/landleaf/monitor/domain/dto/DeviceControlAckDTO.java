package com.landleaf.monitor.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备控制的返回的DTO
 */
@Data
@Schema(name = "DeviceControlAckDTO对象", description = "设备控制的返回的参数封装")
public class DeviceControlAckDTO {

    /**
     * 与执行段通讯时的msgId,与页面交互时不需要
     */
    private String msgId;

    /**
     * 请求是否处理成功
     */
    private boolean success;

    /**
     * 业务异常错误代码
     */
    private String errorCode;

    /**
     * 业务异常错误信息
     */
    private String errorMsg;
}
