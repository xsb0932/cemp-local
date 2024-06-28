package com.landleaf.job.config;

import com.landleaf.bms.api.DictApi;
import com.landleaf.oauth.api.TenantApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableFeignClients(clients = {
        DictApi.class,
        TenantApi.class
})
public class RpcConfiguration {
}
