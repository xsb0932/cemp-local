package com.landleaf.api.controller;

import com.alibaba.fastjson2.JSON;
import com.landleaf.api.remote.TestRemote;
import com.landleaf.api.service.ITestService;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.kafka.sender.KafkaSender;
import com.landleaf.redis.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "test", description = "测试")
public class Test {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private TestRemote testRemote;

    @Resource
    private ITestService testServiceImpl;

    @Resource
    private InfluxdbTemplate influxdbTemplate;

    @Autowired
    private KafkaSender kafkaSender;

    @PostConstruct
    public void init() {
        System.out.println("===================");
    }

    @GetMapping("/test")
    @Operation(summary = "test", description = "测试方法")
    public String test(HttpServletRequest req, String id) {
        redisUtils.set("test", "testttt");
        System.out.println(redisUtils.get("test"));
        System.out.println(req.getHeader("X-Trace-Id"));
        testRemote.test();

        System.out.println(JSON.toJSONString(testServiceImpl.page(1, 2)));

//        TestEntity entity = TestEntity.builder().value(10).code("testcode").deviceId(11L).build();
//        influxdbTemplate.write(entity);

        for (int i = 0; i < 1; i++) {
            kafkaSender.send("test-topic", "======================test content" + i);
        }
        return "success";
    }
}
