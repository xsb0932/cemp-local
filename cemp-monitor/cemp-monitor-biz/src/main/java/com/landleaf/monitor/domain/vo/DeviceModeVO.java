package com.landleaf.monitor.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * DeviceModeEntity对象的展示信息封装
 *
 * @author hebin
 * @since 2023-09-14
 */
@Data
@Schema(name = "DeviceModeVO", description = "DeviceModeEntity对象的展示信息封装")
public class DeviceModeVO {

/**
 * id
 */
        @Schema(description = "id")
    private Long id;

/**
 * 设备业务id
 */
        @Schema(description = "设备业务id")
    private String bizDeviceId;

/**
 * 模式代码 1 手动模式 2 夏季模式 3冬季模式
 */
        @Schema(description = "模式代码 1 手动模式 2 夏季模式 3冬季模式")
    private String modeCode;

/**
 * 模式说明
 */
        @Schema(description = "模式说明")
    private String modeDesc;

/**
 * 租户id
 */
        @Schema(description = "租户id")
    private Integer tenantId;
}