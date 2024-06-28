package com.landleaf.gw;

import com.google.common.collect.Maps;
import com.landleaf.gw.service.JjRemoteService;
import com.landleaf.gw.service.impl.JjRemoteServiceImpl;
import com.landleaf.gw.task.AirConditionTask;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.util.MeasurementFindUtil;
import jakarta.annotation.Resource;
import org.influxdb.dto.Point;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Map;

@SpringBootTest
public class GwJinjiangApplicationTest {

    @Resource
    private AirConditionTask airConditionTask;

    @Resource
    private InfluxdbTemplate influxdbTemplate;

    @Resource
    private JjRemoteService jjRemoteServiceImpl;

    @Test
    public void testInsert() {
        System.out.println(influxdbTemplate.getLast(null, "device_status_PK00000001", null, new Date()));
        System.out.println(influxdbTemplate.getLastByTag(null, "device_status_PK00000001", "biz_device_id", null, new Date()));
//        Map<String, String> tags = Maps.newHashMap();
//        tags.put("deviceId", "D0001");
//        tags.put("prodCode", "PK0001");
//
//        Map<String, Object> field = Maps.newHashMap();
//        field.put("P", "200");
//
//        influxdbTemplate.insert(Point.measurement(MeasurementFindUtil.getDeviceStatusMeasurementByProdCode("P0001")).tag(tags)
//                .fields(field)
//                .build());
    }

    @Test
    public void testControllerAIrCondition() {
        jjRemoteServiceImpl.writeCmd(null, null);
    }

    @Test
    public void testGetAirConditionToken() {
        airConditionTask.getDeviceList();
    }
}
