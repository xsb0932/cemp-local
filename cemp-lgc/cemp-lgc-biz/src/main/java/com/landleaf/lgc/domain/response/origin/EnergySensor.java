package com.landleaf.lgc.domain.response.origin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 空气质量
 *
 * @author xushibai
 * @since 2023/09/05
 **/
@Data
public class EnergySensor {

    @Schema(description = "id")
    private String id;

    /**
     *
     */
    @Schema(description = "co2")
    private String ssCo2;
    /**
     *
     */
    @Schema(description = "voc")
    private String ssVoc;
    /**
     *
     */
    @Schema(description = "pm25")
    private String ssPm25;

    /**
     *
     */
    @Schema(description = "温度")
    private String ssTemp;

    /**
     *
     */
    @Schema(description = "湿度")
    private String ssHum;

    @Schema(description = "ssHcho")
    private String ssHcho;

    @Schema(description = "ssHchoLevel")
    private String ssHchoLevel;

    @Schema(description = "ssVocLevel")
    private String ssVocLevel;


}
