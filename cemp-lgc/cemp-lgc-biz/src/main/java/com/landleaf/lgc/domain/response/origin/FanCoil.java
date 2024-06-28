package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 设备运行状态
 *
 * @author xushibai
 * @since 2023/09/05
 **/
public class FanCoil {

    /**
     * 空调内机总数
     */
    @Schema(description = "空调内机总数")
    private String totalNum;

    /**
     * 已开启数量
     */
    @Schema(description = "已开启数量")
    private String onNum;

}
