package com.landleaf.monitor.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "ProjectWeatherResponse", description = "项目天气返回视图")
@Data
public class ProjectWeatherResponse {
    @Schema(description = "风力等级")
    private String windLevel;

    @Schema(description = "当前气温")
    private String temp;

    @Schema(description = "最低气温")
    private String minTemp;

    @Schema(description = "最高气温")
    private String maxTemp;

    @Schema(description = "天气状况")
    private String weatherStatus;

    @Schema(description = "天气图片")
    private String picUrl;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "PM2.5指数")
    private String pm25;

    @Schema(description = "")
    private String newPicUrl;

    @Schema(description = "湿度")
    private String humidity;

    @Schema(description = "风向")
    private String windDirection;

    @Schema(description = "空气品质值")
    private String aqi;

    @Schema(description = "空气品质")
    private String aqiGrade;

    @Schema(description = "深色图标")
    private String blueUrl;

    @Schema(description = "深色图标")
    private String whiteUrl;
}
