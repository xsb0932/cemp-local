package com.landleaf.data.api.device;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.device.dto.WeatherHistoryDTO;
import com.landleaf.data.api.device.dto.WeatherHistoryQueryDTO;
import com.landleaf.data.api.device.dto.WeatherStaQueryDTO;
import com.landleaf.data.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.landleaf.data.enums.ApiConstants.PREFIX;


@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 环境历史数据")
public interface WeatherHistoryApi {

    @PostMapping(PREFIX + "/weather-history")
    @Operation(summary = "环境历史数据")
    Response<List<WeatherHistoryDTO>> getWeatherHistory(@RequestBody WeatherHistoryQueryDTO queryDTO);

    @PostMapping(PREFIX + "/weather-history/everage")
    @Operation(summary = "环境历史区间数据")
    Response<List<WeatherHistoryDTO>> getWeatherHistoryEverage(@RequestBody WeatherStaQueryDTO queryDTO);


}
