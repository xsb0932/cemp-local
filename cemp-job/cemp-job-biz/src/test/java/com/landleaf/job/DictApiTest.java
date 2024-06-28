package com.landleaf.job;

import cn.hutool.json.JSONUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.job.api.dto.JobRpcRequest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class DictApiTest {
    @Resource
    private DiscoveryClient discoveryClient;
    @Resource
    private RestTemplate restTemplate;

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
        // 发起请求（attention：restTemplate默认无超时时间）
        return restTemplate.postForObject(fullUrl, body, Response.class);
    }

    @Test
    public void test() {
        Response response = callService("cemp-monitor", "/job/weather/sync", new JobRpcRequest().setJobId(2L).setExecTime(LocalDateTime.now()).setExecType(0).setExecUser(1L));
        System.out.println(response);
    }
}