package com.landleaf.energy.config;

import com.landleaf.bms.api.*;
import com.landleaf.data.api.device.DeviceHistoryApi;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.monitor.api.DeviceParameterApi;
import com.landleaf.monitor.api.DeviceStaApi;
import com.landleaf.monitor.api.MonitorApi;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.UserRpcApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {
        MonitorApi.class,
        ProjectApi.class,
        TenantApi.class,
        DeviceStaApi.class,
        DeviceHistoryApi.class,
        ProjectSpaceApi.class,
        JobLogApi.class,
        MessageApi.class,
        UserProjectApi.class,
        DeviceParameterApi.class,
        CategoryApi.class,
        UserRpcApi.class
})
public class RpcConfiguration {
}
