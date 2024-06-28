package com.landleaf.jjgj.config;

import com.landleaf.energy.api.*;
import com.landleaf.job.api.JobLogApi;
import com.landleaf.monitor.api.AlarmApi;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.UserRpcApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {
        TenantApi.class,
        JobLogApi.class,
        ReportPushApi.class,
        UserRpcApi.class,
        PlanedElectricityApi.class,
        PlannedGasApi.class,
        PlannedWaterApi.class,
        ProjectStaSubitemDayApi.class,
        AlarmApi.class
})
public class RpcConfiguration {
}
