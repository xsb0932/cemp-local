package com.landleaf.data.api.device;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.data.api.device.dto.WeatherHistoryDTO;
import com.landleaf.data.api.device.dto.WeatherHistoryQueryDTO;
import com.landleaf.data.api.device.dto.WeatherStaQueryDTO;
import com.landleaf.influx.condition.WhereCondition;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.enums.SqlKeyword;
import com.landleaf.influx.util.MeasurementFindUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WeatherHistoryApiImpl
 *
 * @author 张力方
 * @since 2023/7/24
 **/
@RestController
@Slf4j
@RequiredArgsConstructor
public class WeatherHistoryApiImpl implements WeatherHistoryApi {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private final InfluxdbTemplate influxdbTemplate;

    @Override
    public Response<List<WeatherHistoryDTO>> getWeatherHistory(WeatherHistoryQueryDTO request) {
        List<WeatherHistoryDTO> result = new ArrayList<>();
        // 表
        String measurement = MeasurementFindUtil.WEATHER_HISTORY;
        // select
        List<String> fields = CollectionUtil.newArrayList("temp", "updateTime", "humidity", "weatherCode");
        // where
        List<WhereCondition> conditions = new ArrayList<>();
        // 前后加2小时查询
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LE).
                value(formatToUtc(request.getPublishTime().plusHours(2))).
                build());
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.GE).
                value(formatToUtc(request.getPublishTime().minusHours(2))).
                build());
        // order
        String order = "time desc";
        List<JSONObject> dateList = influxdbTemplate.query(fields, measurement, conditions, null, order, null, null);
        for (JSONObject entries : dateList) {
            WeatherHistoryDTO weatherHistoryDTO = new WeatherHistoryDTO();
            String updateTime = (String) entries.get("updateTime");
            if (StringUtils.isNotBlank(updateTime)) {
                updateTime = updateTime.replace(" 发布", "");
                LocalDateTime publishTime = LocalDateTime.parse(updateTime, dateTimeFormatter);
                if (!publishTime.isEqual(request.getPublishTime())) {
                    continue;
                }
                weatherHistoryDTO.setUpdateTime(publishTime);
            }
            String weatherCode = (String) entries.get("weatherCode");
            String temperature = (String) entries.get("temp");
            String humidity = (String) entries.get("humidity");
            if (StringUtils.isNotBlank(weatherCode)) {
                weatherHistoryDTO.setWeatherCode(weatherCode);
            }
            if (StringUtils.isNotBlank(temperature)) {
                weatherHistoryDTO.setTemperature(new BigDecimal(temperature));
            }
            if (StringUtils.isNotBlank(humidity)) {
                weatherHistoryDTO.setHumidity(new BigDecimal(humidity));
            }
            result.add(weatherHistoryDTO);
        }
        return Response.success(result);
    }

    private WeatherHistoryDTO getWeatherEverageByDay(String cityName ,LocalDateTime begin , LocalDateTime end){
        WeatherHistoryDTO historyDTO = new WeatherHistoryDTO();
        // 表
        String measurement = MeasurementFindUtil.WEATHER_HISTORY;
        // select
        List<String> fields = CollectionUtil.newArrayList("temp","humidity", "weatherCode");
        // where
        List<WhereCondition> conditions = new ArrayList<>();
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LT).
                value(formatToUtc(end)).
                build());
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.GE).
                value(formatToUtc(begin)).
                build());
        conditions.add(WhereCondition.builder().
                field("cityName").
                compareKeyWord(SqlKeyword.EQ).
                value(cityName).
                build());
        // order
        String order = "time desc";
        List<JSONObject> dateList = influxdbTemplate.query(fields, measurement, conditions, null, order, null, null);
        if(dateList != null && dateList.size() > 0){
            List<BigDecimal> result =  dateList.stream().map(new Function<JSONObject, BigDecimal>() {
                @Override
                public BigDecimal apply(JSONObject entries) {
                    return entries.get("temp") == null ? null : new BigDecimal((String) entries.get("temp"));
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            //historyDTO.setHumidity(NumberUtil.div(result.stream().reduce(BigDecimal.ZERO, BigDecimal::add),result.size(), 2,RoundingMode.HALF_UP));
            historyDTO.setTemperature(NumberUtil.div(result.stream().reduce(BigDecimal.ZERO, BigDecimal::add),result.size(), 2,RoundingMode.HALF_UP));
        }else{
            historyDTO.setTemperature(BigDecimal.ZERO);
        }
        return historyDTO;
    }

    /**
     * 当月每天的湿度列表
     * @param cityName
     * @param ym
     * @return
     */
    private WeatherHistoryDTO getWeatherEverageByMonth(String cityName , YearMonth ym){
        LocalDateTime begin = LocalDateTime.of(ym.getYear(),ym.getMonthValue(),1,0,0,0);
        LocalDateTime end = begin.plusMonths(1);
        WeatherHistoryDTO historyDTO = new WeatherHistoryDTO();
        // 表
        String measurement = MeasurementFindUtil.WEATHER_HISTORY;
        // select
        List<String> fields = CollectionUtil.newArrayList("temp","humidity", "weatherCode");
        // where
        List<WhereCondition> conditions = new ArrayList<>();
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LT).
                value(formatToUtc(end)).
                build());
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.GE).
                value(formatToUtc(begin)).
                build());
        conditions.add(WhereCondition.builder().
                field("cityName").
                compareKeyWord(SqlKeyword.EQ).
                value(cityName).
                build());
        // order
        String order = "time desc";
        List<JSONObject> dateList = influxdbTemplate.query(fields, measurement, conditions, null, order, null, null);
        if(dateList != null && dateList.size()>0){
            List<BigDecimal> result =  dateList.stream().map(new Function<JSONObject, BigDecimal>() {
                @Override
                public BigDecimal apply(JSONObject entries) {
                    return entries.get("temp") == null ? null : new BigDecimal((String) entries.get("temp"));
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            historyDTO.setTemperature(NumberUtil.div(result.stream().reduce(BigDecimal.ZERO, BigDecimal::add),result.size(),2,RoundingMode.HALF_UP));
        }else{
            historyDTO.setTemperature(BigDecimal.ZERO);
        }
        return historyDTO;
    }


    @Override
    public Response<List<WeatherHistoryDTO>> getWeatherHistoryEverage(@RequestBody WeatherStaQueryDTO request) {
        List<WeatherHistoryDTO> result = new ArrayList<>();
        if(request.getType() == 1){
            // 查询当月天数据
            YearMonth ym = request.getYms().get(0);
            YearMonth ymNext = ym.plusMonths(1);
            List<LocalDate> dates = DateUtils.getDaysBetween(LocalDate.of(ym.getYear(),ym.getMonthValue(),1),LocalDate.of(ymNext.getYear(),ymNext.getMonthValue(),1) );
            result =dates.stream().map(date -> {
                LocalDateTime begin = date.atStartOfDay();
                LocalDateTime end = begin.plusDays(1);
                WeatherHistoryDTO dto = getWeatherEverageByDay(request.getCityName(),begin,end);
                dto.setStaTime(String.valueOf(date.getDayOfMonth()));
                return dto;
            }).sorted((o1, o2) -> Integer.valueOf(o1.getStaTime()).compareTo(Integer.valueOf(o2.getStaTime()))).collect(Collectors.toList());

        }else{
            //查询当年月数据
            List<YearMonth> months = request.getYms();
            result = months.stream().map(new Function<YearMonth, WeatherHistoryDTO>() {
                @Override
                public WeatherHistoryDTO apply(YearMonth yearMonth) {
                    WeatherHistoryDTO dto =  getWeatherEverageByMonth(request.getCityName(),yearMonth);
                    dto.setStaTime(String.valueOf(yearMonth.getMonthValue()));
                    return dto;
                }
            }).sorted((o1, o2) -> Integer.valueOf(o1.getStaTime()).compareTo(Integer.valueOf(o2.getStaTime()))).collect(Collectors.toList());

        }
        return Response.success(result);
    }

    private String formatToUtc(LocalDateTime time) {
        return DateFormatUtils.formatUTC(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern());
    }

    private LocalDateTime paresToLocal(String utcTime) {
        return LocalDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

}
