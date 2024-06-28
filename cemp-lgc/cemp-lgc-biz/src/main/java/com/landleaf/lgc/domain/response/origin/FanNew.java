package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 设备运行状态
 *
 * @author xushibai
 * @since 2023/09/05
 **/
public class FanNew {

    /**
     * id
     */
    @Schema(description = "id")
    private String id;

    /**
     * 开关标记
     */
    @Schema(description = "开关标记")
    private String onOff;
    /**
     * 运行模式
     */
    @Schema(description = "运行模式")
    private String runningMode;
    /**
     * 运行模式-中文描述
     */
    @Schema(description = "运行模式-中文描述")
    private String runningModeStr;

}
