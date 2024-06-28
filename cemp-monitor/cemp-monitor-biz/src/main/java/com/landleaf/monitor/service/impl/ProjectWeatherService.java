package com.landleaf.monitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.comm.util.WeatherUtil;
import com.landleaf.monitor.dal.redis.WeatherCacheRedisDAO;
import com.landleaf.monitor.domain.dto.WeatherDTO;
import com.landleaf.monitor.domain.response.ProjectWeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Yang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectWeatherService {
    private final WeatherCacheRedisDAO weatherCacheRedisDAO;
    private final ProjectWeatherApi projectWeatherApi;

    public ProjectWeatherResponse getByProject(String bizProjectId) {
        ProjectWeatherResponse response = new ProjectWeatherResponse();
        String weatherName = projectWeatherApi.getProjectWeatherName(bizProjectId).getCheckedData();
        if (StrUtil.isBlank(weatherName)) {
            return response;
        }
        WeatherDTO weatherDTO = weatherCacheRedisDAO.getWeatherByWeatherName(weatherName);
        if (null != weatherDTO) {
            BeanUtil.copyProperties(weatherDTO, response);
            response.setBlueUrl(WeatherUtil.replace(response.getBlueUrl()));
            response.setPicUrl(WeatherUtil.replace(response.getPicUrl()));
            response.setWhiteUrl(WeatherUtil.replace(response.getWhiteUrl()));
        }
        return response;
    }
}
