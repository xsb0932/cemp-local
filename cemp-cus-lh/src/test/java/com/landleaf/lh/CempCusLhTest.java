package com.landleaf.lh;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.landleaf.lh.domain.response.*;
import com.landleaf.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CempCusLhTest {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void mockLightBoardData() {
        // 主机
        String engineKey = "mock.engine";
        List<LightBoardEngineProjectResponse> engineProjectList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            LightBoardEngineProjectResponse project = new LightBoardEngineProjectResponse()
                    .setBizProjectId("P0000000" + i)
                    .setProjectName("模拟项目" + i);
            engineProjectList.add(project);

            List<LightBoardEngineResponse> engineList = new ArrayList<>();
            project.setEngineList(engineList);
            for (int j = 0; j < 9; j++) {
                LightBoardEngineResponse engine = new LightBoardEngineResponse()
                        .setBizDeviceId("D0000000" + j)
                        .setDeviceName("模拟主机" + j)
                        .setCst(RandomUtil.randomInt(0, 2))
                        .setPlr(String.valueOf(RandomUtil.randomInt(0, 100)))
                        .setPumpRST(String.valueOf(RandomUtil.randomInt(0, 2)))
                        .setHasUCAlarm(RandomUtil.randomBoolean())
                        .setHasAlarm(RandomUtil.randomBoolean())
                        .setEvaporatingValveOpenedFlag(RandomUtil.randomBoolean() ? "1" : "0")
                        .setEvaporatingInTemp(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setEvaporatingInTempUnit("℃")
                        .setEvaporatingOutTemp(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setEvaporatingOutTempUnit("℃")
                        .setCondensingValveOpenedFlag(RandomUtil.randomBoolean() ? "1" : "0")
                        .setCondensingInTemp(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setCondensingInTempUnit("℃")
                        .setCondensingOutTemp(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setCondensingOutTempUnit("℃");
                engineList.add(engine);
            }
        }
        redisUtils.set(engineKey, JSONUtil.toJsonStr(engineProjectList));
        // 新风
        String freshAirKey = "mock.freshAir";
        List<LightBoardFreshAirProjectResponse> freshAirProjectList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            LightBoardFreshAirProjectResponse project = new LightBoardFreshAirProjectResponse()
                    .setBizProjectId("P0000000" + i)
                    .setProjectName("模拟项目" + i);
            freshAirProjectList.add(project);

            List<LightBoardFreshAirResponse> freshAirList = new ArrayList<>();
            project.setFreshAirList(freshAirList);
            for (int j = 0; j < 9; j++) {
                LightBoardFreshAirResponse freshAir = new LightBoardFreshAirResponse()
                        .setBizDeviceId("D0000000" + j)
                        .setDeviceName("模拟新风机" + j)
                        .setCst(RandomUtil.randomInt(0, 2))
                        .setSupplyAirHumidity(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setSupplyAirHumidityUnit("%")
                        .setSupplyAirHumidityHasUCAlarm(RandomUtil.randomBoolean())
                        .setSupplyAirHumidityHasAlarm(RandomUtil.randomBoolean())
                        .setSupplyAirTemp(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setSupplyAirTempUnit("℃")
                        .setSupplyAirTempHasUCAlarm(RandomUtil.randomBoolean())
                        .setSupplyAirTempHasAlarm(RandomUtil.randomBoolean());
                freshAirList.add(freshAir);
            }
        }
        redisUtils.set(freshAirKey, JSONUtil.toJsonStr(freshAirProjectList));
        // 回路
        String circuitKey = "mock.circuit";
        List<LightBoardCircuitProjectResponse> circuitProjectList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            LightBoardCircuitProjectResponse project = new LightBoardCircuitProjectResponse()
                    .setBizProjectId("P0000000" + i)
                    .setProjectName("模拟项目" + i);
            circuitProjectList.add(project);

            List<LightBoardCircuitNormalResponse> normalList = new ArrayList<>();
            project.setNormalList(normalList);

            List<LightBoardCircuitHotWaterSpecialResponse> hotWaterList1 = new ArrayList<>();
            project.setHotWaterList1(hotWaterList1);

            List<LightBoardCircuitHotWaterResponse> hotWaterList2 = new ArrayList<>();
            project.setHotWaterList2(hotWaterList2);
            for (int j = 0; j < 5; j++) {
                LightBoardCircuitNormalResponse normal = new LightBoardCircuitNormalResponse()
                        .setBizDeviceId("D0000000" + j)
                        .setDeviceName("普通回路" + j)
                        .setCst(RandomUtil.randomInt(0, 2))
                        .setReturnWaterPressure(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(50))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setReturnWaterPressureUnit("bar")
                        .setSupplyWaterPressure(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(50))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setSupplyWaterPressureUnit("bar");
                List<LightBoardCircuitPumpResponse> pumpList1 = new ArrayList<>();
                normal.setPumpList(pumpList1);
                for (int k = 0; k < 4; k++) {
                    LightBoardCircuitPumpResponse pump = new LightBoardCircuitPumpResponse()
                            .setBizDeviceId("D0000000" + k)
                            .setDeviceName("水泵" + k)
                            .setCst(RandomUtil.randomInt(0, 2))
                            .setPumpRST(String.valueOf(RandomUtil.randomInt(0, 2)))
                            .setF(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .toString())
                            .setFUnit("hz")
                            .setHasUCAlarm(RandomUtil.randomBoolean())
                            .setHasAlarm(RandomUtil.randomBoolean());

                    pumpList1.add(pump);
                }
                normalList.add(normal);

                LightBoardCircuitHotWaterSpecialResponse hotWaterSpecial = new LightBoardCircuitHotWaterSpecialResponse()
                        .setBizDeviceId("D0000000" + j)
                        .setDeviceName("热水一次/二次回路" + j)
                        .setCst(1);

                List<LightBoardCircuitPumpResponse> pumpList2 = new ArrayList<>();
                hotWaterSpecial.setPumpList(pumpList2);
                for (int k = 0; k < 3; k++) {
                    LightBoardCircuitPumpResponse pump = new LightBoardCircuitPumpResponse()
                            .setBizDeviceId("D0000000" + k)
                            .setDeviceName("水泵" + k)
                            .setCst(RandomUtil.randomInt(0, 2))
                            .setPumpRST(String.valueOf(RandomUtil.randomInt(0, 2)))
                            .setF(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .toString())
                            .setFUnit("hz")
                            .setHasUCAlarm(RandomUtil.randomBoolean())
                            .setHasAlarm(RandomUtil.randomBoolean());

                    pumpList2.add(pump);
                }
                hotWaterList1.add(hotWaterSpecial);

                LightBoardCircuitHotWaterResponse hotWater = new LightBoardCircuitHotWaterResponse()
                        .setBizDeviceId("D0000000" + j)
                        .setDeviceName("热水回路" + j)
                        .setCst(RandomUtil.randomInt(0, 2))
                        .setSupplyWaterTemp(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(50))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setSupplyWaterTempUnit("℃")
                        .setSupplyWaterPressure(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(50))
                                .setScale(2, RoundingMode.HALF_UP)
                                .toString())
                        .setSupplyWaterPressureUnit("bar");
                List<LightBoardCircuitPumpResponse> pumpList3 = new ArrayList<>();
                hotWater.setPumpList(pumpList3);
                for (int k = 0; k < 2; k++) {
                    LightBoardCircuitPumpResponse pump = new LightBoardCircuitPumpResponse()
                            .setBizDeviceId("D0000000" + k)
                            .setDeviceName("水泵" + k)
                            .setCst(RandomUtil.randomInt(0, 2))
                            .setPumpRST(String.valueOf(RandomUtil.randomInt(0, 2)))
                            .setF(RandomUtil.randomBigDecimal(BigDecimal.ZERO, BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .toString())
                            .setFUnit("hz")
                            .setHasUCAlarm(RandomUtil.randomBoolean())
                            .setHasAlarm(RandomUtil.randomBoolean());

                    pumpList3.add(pump);
                }
                hotWaterList2.add(hotWater);
            }
        }
        redisUtils.set(circuitKey, JSONUtil.toJsonStr(circuitProjectList));
    }

    @Test
    public void devTestData() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> cst = new HashMap<>();
        String prefix = "device_current_status_v1:";

        String key = prefix + "D000000001301";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        Map<String, Object> val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", new BigDecimal("70.5"));
        properties.put("plr", val);
        Map<String, Object> val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", new BigDecimal("20.5"));
        properties.put("condensingInTemp", val2);
        Map<String, Object> val3 = new HashMap<>();
        val3.put("time", 1714375419910L);
        val3.put("val", new BigDecimal("30.5"));
        properties.put("condensingOutTemp", val3);
        Map<String, Object> val4 = new HashMap<>();
        val4.put("time", 1714375419910L);
        val4.put("val", new BigDecimal("40.5"));
        properties.put("evaporatingInTemp", val4);
        Map<String, Object> val5 = new HashMap<>();
        val5.put("time", 1714375419910L);
        val5.put("val", new BigDecimal("50.5"));
        properties.put("evaporatingOutTemp", val5);
        Map<String, Object> val6 = new HashMap<>();
        val6.put("time", 1714375419910L);
        val6.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumpRST", val6);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001286";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", 1);
        properties.put("valveOpenedFlag", val);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001287";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", 1);
        properties.put("valveOpenedFlag", val);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001288";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", new BigDecimal("40.0"));
        properties.put("supplyAirHumidity", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", new BigDecimal("26.0"));
        properties.put("supplyAirTemp", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001289";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", new BigDecimal("30.0"));
        properties.put("returnWaterPressure", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", new BigDecimal("50.0"));
        properties.put("supplyWaterPressure", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001290";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001291";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001292";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001293";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001294";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001295";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001296";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001297";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001298";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("supplyWaterTemp", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("supplyWaterPressure", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001299";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();

        key = prefix + "D000000001300";
        cst.put("val", 1);
        cst.put("time", 1714375419910L);
        map.put("CST", cst);

        val = new HashMap<>();
        val.put("time", 1714375419910L);
        val.put("val", RandomUtil.randomInt(0, 2));
        properties.put("pumbRST", val);
        val2 = new HashMap<>();
        val2.put("time", 1714375419910L);
        val2.put("val", RandomUtil.randomBigDecimal(BigDecimal.ZERO, new BigDecimal("100")));
        properties.put("F", val2);
        map.put("propertys", properties);
        redisTemplate.opsForHash().putAll(key, map);

        cst.clear();
        properties.clear();
        map.clear();
    }

    @Test
    public void test1() {
//        String key1 = "device_current_status_v1:D000000001397";
//        String key2 = "device_current_status_v1:D000000001399";
//        redisUtils.del(key1);
//        redisUtils.del(key2);
    }
}
