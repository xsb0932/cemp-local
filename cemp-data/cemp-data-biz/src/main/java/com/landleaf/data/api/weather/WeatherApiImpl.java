package com.landleaf.data.api.weather;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.data.api.weather.dto.WeatherHistoryDTO;
import com.landleaf.influx.core.InfluxdbTemplate;
import lombok.RequiredArgsConstructor;
import org.influxdb.dto.Point;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.landleaf.data.constants.MeasurementConstant.WEATHER_HISTORY;

/**
 * @author Yang
 */
@RestController
@RequiredArgsConstructor
public class WeatherApiImpl implements WeatherApi {
    private final InfluxdbTemplate influxdbTemplate;

    @Override
    public Response<Void> saveWeatherHistory(Collection<WeatherHistoryDTO> weatherHistoryList) {
        for (WeatherHistoryDTO weatherHistoryDTO : weatherHistoryList) {
            Map<String, String> tags = weatherHistoryDTO.getTags();
            Map<String, Object> fields = weatherHistoryDTO.getFields();

            influxdbTemplate.insert(
                    Point.measurement(WEATHER_HISTORY)
                            .time(weatherHistoryDTO.getTimestamp(), TimeUnit.MILLISECONDS)
                            .tag(tags)
                            .fields(fields)
                            .build()
            );
        }
        return Response.success();
    }
}
