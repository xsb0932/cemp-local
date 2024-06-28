package com.landleaf.monitor.config;

import com.landleaf.bms.api.*;
import com.landleaf.bms.api.weather.ProjectWeatherApi;
import com.landleaf.data.api.device.DeviceCurrentApi;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.data.api.weather.WeatherApi;
import com.landleaf.file.api.FileApi;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.messaging.api.ServiceControlApi;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.UserRpcApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {
        DeviceCurrentApi.class,
        UserProjectApi.class,
        DeviceHistoryApi.class,
        ProjectWeatherApi.class,
        WeatherApi.class,
        DictApi.class,
        JobLogApi.class,
        TenantApi.class,
        UserManagementNodeApi.class,
        DeviceIotApi.class,
        ProductApi.class,
        ProjectSpaceApi.class,
        UserRpcApi.class,
        FileApi.class,
        ServiceControlApi.class,
        CategoryApi.class
})
public class RpcConfiguration {
}
