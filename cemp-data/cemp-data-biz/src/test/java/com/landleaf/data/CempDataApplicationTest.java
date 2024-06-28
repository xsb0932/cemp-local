package com.landleaf.data;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.data.api.device.*;
import com.landleaf.data.api.device.dto.*;
import com.landleaf.influx.core.InfluxdbTemplate;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@SpringBootTest
public class CempDataApplicationTest {
    @Resource
    private DeviceHistoryApi deviceHistoryApiImpl;

    @Resource
    private DeviceCurrentApi deviceCurrentApiImpl;

    @Resource
    private WeatherHistoryApi weatherHistoryApiImpl;

    @Test
    void getWeather() {
        WeatherHistoryQueryDTO dto = new WeatherHistoryQueryDTO();
        dto.setPublishTime(LocalDateTime.now());
        System.out.println(JSON.toJSONString(weatherHistoryApiImpl.getWeatherHistory(dto)));
    }

    @Test
    void testAlarmCount(){
        System.out.println(
                JSON.toJSONString(deviceCurrentApiImpl.getDeviceCurrentAlarm(Lists.newArrayList("D000000001356"))));
    }

    @Test
    void testLatestData() {
        StaLatestDataRequest request =  StaLatestDataRequest.builder()
                .time(LocalDateTime.of(2023, 9, 26, 8, 0, 0))
                .bizDeviceId("D000000001233")
                .bizProductId("PK00001067")
                .field("CST")
                .build();
//        StaLatestDataRequest request = new StaLatestDataRequest();
//        request.setField("CST");
//        request.setTime(LocalDateTime.of(2023, 9, 26, 8, 0, 0));
//        request.setBizProductId("PK00001067");
//        request.setBizDeviceId("D000000001233");
        Response<String> data = deviceHistoryApi.getLatestData(request);
        System.out.println(data.getResult());
    }

    @Test
    public void testHistoryQuery() {
        HistoryQueryInnerDTO queryDTO = new HistoryQueryInnerDTO();
        queryDTO.setBizProductId("PK00000001");
        queryDTO.setBizDeviceIds("D000000000003,D000000000004,D000000000005,D000000000006,D000000000061");
        queryDTO.setAttrCode("Uca,CST");
        queryDTO.setStartTime("2023-06-19 00:00:00");
        queryDTO.setEndTime("2023-06-25 23:59:59");
        queryDTO.setPeriodType(1);
        Response resp = deviceHistoryApiImpl.getDeviceHistory(queryDTO);
        System.out.println(JSONUtil.toJsonStr(resp));
    }

    @Resource
    private InfluxdbTemplate influxdbTemplate;

    @Test
    public void testHistoryQuery2() {
        //TODO 记录一个毫秒转换的问题
//        List<JSONObject> jsonObjects = influxdbTemplate.queryBase("select * from weather_history");
//        for (JSONObject jsonObject : jsonObjects) {
//            System.out.println(jsonObject);
//        }
//        List<WeatherHistoryDTO> weatherHistoryDTOS = influxdbTemplate.queryBase("select * from weather_history", WeatherHistoryDTO.class);
//        for (WeatherHistoryDTO weatherHistoryDTO : weatherHistoryDTOS) {
//            System.out.println(weatherHistoryDTO);
//        }
//        Map<String, String> tags = Maps.newHashMap();
//        tags.put("id", "1");
//        Map<String, Object> fields = Maps.newHashMap();
//        fields.put("temp", 23);
//        LocalDateTime now = LocalDateTime.of(2023, 6, 26, 0, 0, 0);
//
//        influxdbTemplate.insert(
//                Point.measurement("test_time")
//                        .time(LocalDateTimeUtil.toEpochMilli(now), TimeUnit.MILLISECONDS)
//                        .tag(tags)
//                        .fields(fields)
//                        .build()
//        );
        String sql = "SELECT \n" +
                "biz_device_id, CST,RST,Temperature \n" +
                "FROM device_status_PK00000004\n" +
                "GROUP BY biz_device_id\n" +
                "ORDER BY time ASC \n" +
                "\n" +
                ";";
        List<JSONObject> jsonObjects = influxdbTemplate.queryBase(sql);
        for (JSONObject jsonObject : jsonObjects) {
            System.out.println(jsonObject);
        }
    }

    @Resource
    private DeviceHistoryApiImpl deviceHistoryApi;
    @Resource
    private WeatherHistoryApiImpl weatherHistoryApi;

    @Test
    public void testAirHistoryQuery() {
        StaDeviceBaseRequest request = new StaDeviceBaseRequest();
        HashMap<String, List<String>> prodDeviceIds = new HashMap<>();
        prodDeviceIds.put("PK00000004", CollectionUtil.newArrayList("D000000000048"));

        request.setStart(LocalDateTime.of(2023, 7, 4, 9, 0, 0))
                .setEnd(LocalDateTime.of(2023, 7, 4, 10, 0, 0))
                .setProductDeviceIds(prodDeviceIds);
        deviceHistoryApi.getStaAirDeviceData(request);
    }

    @Test
    void testElectricity() {
        HashMap<String, List<String>> prodDeviceIds = new HashMap<>();
        prodDeviceIds.put("PK00001052", CollectionUtil.newArrayList("D000000001237", "D000000001238", "D000000001239", "D000000001240", "D000000001241", "D000000001242"));
        StaDeviceBaseRequest staDeviceBaseRequest = new StaDeviceBaseRequest();
        staDeviceBaseRequest.setStart(LocalDateTime.of(2023, 8, 25, 0, 0, 0));
        staDeviceBaseRequest.setEnd(LocalDateTime.of(2023, 8, 25, 23, 0, 0));
        staDeviceBaseRequest.setProductDeviceIds(prodDeviceIds);
        Response<List<StaDeviceElectricityResponse>> staElectricityDeviceData = deviceHistoryApi.getStaElectricityDeviceData(staDeviceBaseRequest);
        System.out.println(staElectricityDeviceData);
    }

    @Test
    void testZnb() {
        HashMap<String, List<String>> prodDeviceIds = new HashMap<>();
        prodDeviceIds.put("PK00001067", CollectionUtil.newArrayList("D000000001233"));
        StaDeviceBaseRequest staDeviceBaseRequest = new StaDeviceBaseRequest();
        staDeviceBaseRequest.setStart(LocalDateTime.of(2023, 8, 28, 0, 0, 0));
        staDeviceBaseRequest.setEnd(LocalDateTime.of(2023, 8, 28, 23, 0, 0));
        staDeviceBaseRequest.setProductDeviceIds(prodDeviceIds);
        Response<List<StaDeviceZnbResponse>> staZnbDeviceData = deviceHistoryApi.getStaZnbDeviceData(staDeviceBaseRequest);
        System.out.println(staZnbDeviceData);
    }

    @Test
    void testWeatherHistory() {
        WeatherHistoryQueryDTO weatherHistoryQueryDTO = new WeatherHistoryQueryDTO();
        weatherHistoryQueryDTO.setPublishTime(LocalDateTime.of(2024, 5, 21, 14, 0, 0));
        Response<List<WeatherHistoryDTO>> deviceHistory = weatherHistoryApi.getWeatherHistory(weatherHistoryQueryDTO);
        List<WeatherHistoryDTO> result = deviceHistory.getResult();
        for (WeatherHistoryDTO weatherHistoryDTO : result) {
            System.out.println(weatherHistoryDTO);
        }
    }

    @Test
    void testWeatherEverage() {

        WeatherStaQueryDTO queryDTO = new WeatherStaQueryDTO();
        List<YearMonth> list = new ArrayList<>();
        //list.add(YearMonth.of(2024,1));       //测试type1
        list = DateUtils.getMonthsBetween(YearMonth.of(2024,1),YearMonth.of(2025,1));//测试type2

        queryDTO.setYms(list);
        queryDTO.setCityName("上海");
        queryDTO.setType(2);

        Response<List<WeatherHistoryDTO>> deviceHistory = weatherHistoryApi.getWeatherHistoryEverage(queryDTO);
        System.out.println("success");
    }

    @Test
    void testZnbP() {
        BasePRequest request = new BasePRequest();
        request.setDeviceIds(Arrays.asList("D000000000017", "D000000000019"));
        Response<List<ZnbPResponse>> deviceHistory = deviceHistoryApi.getZnbPResponse(request);
        for (ZnbPResponse znbPResponse : deviceHistory.getResult()) {
            System.out.println(znbPResponse);
        }
    }

    @Test
    void testGSCN() {
        StaDeviceBaseRequest staDeviceBaseRequest = new StaDeviceBaseRequest();
        LinkedHashMap<String, List<String>> productDeviceIds = new LinkedHashMap<>();
        productDeviceIds.put("PK00001068", Collections.singletonList("D000000001232"));
        staDeviceBaseRequest.setProductDeviceIds(productDeviceIds);
        staDeviceBaseRequest.setStart(LocalDateTime.of(2023, 8, 24, 11, 0));
        staDeviceBaseRequest.setEnd(LocalDateTime.of(2023, 8, 24, 12, 0));
        Response<List<StaDeviceGscnResponse>> staGscnDeviceData = deviceHistoryApi.getStaGscnDeviceData(staDeviceBaseRequest);
        System.out.println(staGscnDeviceData);
    }

    @Test
    void testStation() {
        BasePRequest basePRequest = new BasePRequest();
        basePRequest.setProductBizId("PK00001052");
        List<String> bizDeviceIds = Arrays.asList("D000000001238", "D000000001239", "D000000001240", "D000000001241", "D000000001242", "D000000001237");
        basePRequest.setDeviceIds(bizDeviceIds);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
        basePRequest.setStart(start);
        basePRequest.setEnd(start.plusDays(1L));
        Response<List<ChargePResponse>> chargePResponse = deviceHistoryApi.getChargePResponse(basePRequest);
        List<ChargePResponse> result = chargePResponse.getResult();
        for (ChargePResponse pResponse : result) {
            System.out.println(pResponse);
        }
    }
}
