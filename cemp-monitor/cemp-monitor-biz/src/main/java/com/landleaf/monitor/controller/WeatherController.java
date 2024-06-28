package com.landleaf.monitor.controller;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.monitor.domain.response.ProjectWeatherResponse;
import com.landleaf.monitor.service.impl.ProjectWeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yang
 */
@RestController
@AllArgsConstructor
@RequestMapping("/weather")
@Tag(name = "天气的控制层接口定义", description = "天气的控制层接口定义")
public class WeatherController {
    private ProjectWeatherService projectWeatherService;

    @GetMapping("/get-by-project/{bizProjectId}")
    @Operation(summary = "获取项目所在城市的天气")
    public Response<ProjectWeatherResponse> getByProject(@PathVariable("bizProjectId") String bizProjectId) {
        return Response.success(projectWeatherService.getByProject(bizProjectId));
    }
}
