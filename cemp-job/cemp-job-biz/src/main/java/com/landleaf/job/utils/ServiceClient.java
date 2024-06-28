package com.landleaf.job.utils;

import cn.hutool.json.JSONUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.api.dto.JobRpcRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceClient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    public String getServiceUri(String serviceName) {
        // 获取指定服务名的所有服务实例
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            throw new IllegalArgumentException("No instances available for service: " + serviceName);
        }
        // 获取第一个服务实例的URI
        return instances.get(0).getUri().toString();
    }

    public Response callService(String serviceName, String apiUrl, JobRpcRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> body = new HttpEntity<>(JSONUtil.toJsonStr(request), headers);

        // 获取服务的URI
        String serviceUri = getServiceUri(serviceName);
        // 构建完整的接口URL
        String fullUrl = serviceUri + apiUrl;
        return restTemplate.postForObject(fullUrl, body, Response.class);
    }
}
