package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 累计能耗
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@Data
public class EnergyWeather {

    /**
     * 天气图标
     */
    @Schema(description = "天气图标")
    private String picUrl;

    /**
     * 天气情况
     */
    @Schema(description = "天气情况")
    private String weatherStatus;

    /**
     * 温度
     */
    @Schema(description = "温度")
    private String wsTemp;

    /**
     * 湿度
     */
    @Schema(description = "湿度")
    private String wsHum;

    /**
     * PM25
     */
    @Schema(description = "PM25")
    private String wsPm25;
}
