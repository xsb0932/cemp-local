package com.landleaf.bms.api.weather;

import com.landleaf.bms.api.enums.ApiConstants;
import com.landleaf.bms.api.weather.dto.ProjectCityWeatherDTO;
import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;

/**
 * @author Yang
 */
@Tag(name = "Feign 服务 - 项目天气")
@FeignClient(name = ApiConstants.NAME)
public interface ProjectWeatherApi {

    @GetMapping(ApiConstants.PREFIX + "/list-project-city-weather")
    @Operation(summary = "查询所有项目用到的城市天气")
    Response<Collection<ProjectCityWeatherDTO>> listProjectCityWeather();

    @GetMapping(ApiConstants.PREFIX + "/get-project-weather-name/{bizProjectId}")
    @Operation(summary = "查询项目用到的天气城市名称")
    Response<String> getProjectWeatherName(@PathVariable("bizProjectId") String bizProjectId);
}
