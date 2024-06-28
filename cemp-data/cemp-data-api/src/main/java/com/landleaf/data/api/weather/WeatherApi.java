package com.landleaf.data.api.weather;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.weather.dto.WeatherHistoryDTO;
import com.landleaf.data.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

import static com.landleaf.data.enums.ApiConstants.PREFIX;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 天气")
public interface WeatherApi {

    @PostMapping(PREFIX + "/weather-history-save")
    @Operation(summary = "保存天气历史记录")
    Response<Void> saveWeatherHistory(@RequestBody Collection<WeatherHistoryDTO> weatherHistoryList);
}
