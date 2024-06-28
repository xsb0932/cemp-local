package com.landleaf.monitor.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "原始的天气信息")
public class WeatherDTO {
    @Schema(description = "天气code")
    private String weatherCode;

    @Schema(description = "天气城市名")
    private String weatherName;

    @Schema(description = "发布日期")
    private String date;

    @Schema(description = "星期")
    private String week;

    @Schema(description = "农历日历")
    private String calender;

    @Schema(description = "着装等级")
    private String dressLevel;

    @Schema(description = "风力等级")
    private String windLevel;

    @Schema(description = "当前气温")
    private String temp;

    @Schema(description = "最低气温")
    private String minTemp;

    @Schema(description = "最高气温")
    private String maxTemp;

    @Schema(description = "发布时间")
    private String updateTime;

    @Schema(description = "天气状况")
    private String weatherStatus;

    @Schema(description = "简易版日历时间")
    private String singleCalender;

    @Schema(description = "紫外线强度")
    private String uvLevel;

    @Schema(description = "天气图片")
    private String picUrl;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "城市URL")
    private String cityUrl;

    @Schema(description = "PM2.5指数")
    private String pm25;

    @Schema(description = "")
    private String newPicUrl;

    @Schema(description = "运动等级")
    private String sportLevel;

    @Schema(description = "湿度")
    private String humidity;

    @Schema(description = "风向")
    private String windDirection;

    @Schema(description = "感冒等级")
    private String coldLevel;

    @Schema(description = "描述")
    private String tips;

    @Schema(description = "空气品质值")
    private String aqi;

    @Schema(description = "空气品质")
    private String aqiGrade;

    @Schema(description = "深色图标")
    private String blueUrl;

    @Schema(description = "深色图标")
    private String whiteUrl;
    
}
