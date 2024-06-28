package com.landleaf.monitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.bms.api.weather.dto.ProjectCityWeatherDTO;
import com.landleaf.data.api.weather.WeatherApi;
import com.landleaf.data.api.weather.dto.WeatherHistoryDTO;
import com.landleaf.monitor.dal.redis.WeatherCacheRedisDAO;
import com.landleaf.monitor.domain.dto.WeatherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherJobService {
    private final WeatherCacheRedisDAO weatherCacheRedisDAO;
    private final WeatherApi weatherApi;
    private final ProjectWeatherApi projectWeatherApi;

    private static String URL;

    @Value("${cemp.remote-weather-url}")
    public void setUrl(String url) {
        URL = url;
    }

    /**
     * 定时任务同步当前天气
     *
     * @return 同步的城市
     */
    public Map<String, WeatherDTO> sync() {
        long timestamp = LocalDateTimeUtil.toEpochMilli(LocalDateTime.now());
        Map<String, WeatherDTO> cityWeatherMap = new HashMap<>(16);
        // 获取当前所有项目的天气code 和 天气城市名
        Collection<ProjectCityWeatherDTO> projectCityWeatherDTOList = projectWeatherApi.listProjectCityWeather().getCheckedData();
        // 调用洪滨接口获取当前天气
        for (ProjectCityWeatherDTO projectCityWeatherDTO : projectCityWeatherDTOList) {
            WeatherDTO weatherDTO = remoteGetWeather(projectCityWeatherDTO.getWeatherName());
            if (null != weatherDTO) {
                weatherDTO.setWeatherCode(projectCityWeatherDTO.getWeatherCode())
                        .setWeatherName(projectCityWeatherDTO.getWeatherName());
                cityWeatherMap.put(projectCityWeatherDTO.getWeatherName(), weatherDTO);
            }
        }
        if (MapUtil.isEmpty(cityWeatherMap)) {
            return cityWeatherMap;
        }
        // 更新天气缓存
        weatherCacheRedisDAO.save(cityWeatherMap);
        // 历史数据保存
        weatherApi.saveWeatherHistory(
                cityWeatherMap.values().stream()
                        .map(o -> {
                            WeatherHistoryDTO dto = new WeatherHistoryDTO();
                            BeanUtil.copyProperties(o, dto);
                            dto.setTimestamp(timestamp);
                            return dto;
                        })
                        .collect(Collectors.toList())
        );
        return cityWeatherMap;
    }

    public static WeatherDTO remoteGetWeather(String cityName) {
        if (StrUtil.isBlank(cityName)) {
            return null;
        }
        try {
            String url = StrUtil.format(URL, cityName);
            HttpResponse response = HttpUtil.createPost(url)
                    .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                    .header(Header.ACCEPT_CHARSET, CharsetUtil.UTF_8)
                    .execute();
            String data = response.body();
            if (!response.isOk() || !JSONUtil.isTypeJSON(data)) {
                return null;
            }
            log.debug("获取天气信息:{}:{}", cityName, data);
            return JSONUtil.toBean(response.body(), WeatherDTO.class);
        } catch (Exception e) {
            log.error("获取天气接口异常:{}", cityName, e);
        }
        return null;
    }
}
