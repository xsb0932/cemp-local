package com.landleaf.data.controller;

import cn.hutool.json.JSONObject;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.influx.core.InfluxdbTemplate;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class TestController {
    @Resource
    private InfluxdbTemplate influxdbTemplate;

    @GetMapping("/test")
    public Response<Object> test() {
        List<JSONObject> jsonObjects = influxdbTemplate.queryBase("SELECT * FROM device_status_PC00001");
        return Response.success(jsonObjects);
    }
}
