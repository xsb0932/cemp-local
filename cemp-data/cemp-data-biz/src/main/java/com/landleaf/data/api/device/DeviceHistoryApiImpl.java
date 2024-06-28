package com.landleaf.data.api.device;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Maps;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.PeriodTypeConst;
import com.landleaf.data.api.device.dto.*;
import com.landleaf.influx.condition.WhereCondition;
import com.landleaf.influx.core.InfluxdbTemplate;
import com.landleaf.influx.enums.SqlKeyword;
import com.landleaf.influx.util.MeasurementFindUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class DeviceHistoryApiImpl implements DeviceHistoryApi {

    @Resource
    private InfluxdbTemplate influxdbTemplate;


    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Response<String> getLatestData(StaLatestDataRequest request) {
        List<StaDeviceGasResponse> result = new ArrayList<>();
        String bizProductId = request.getBizProductId();
        String bizDeviceId = request.getBizDeviceId();
        // 表
        String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProductId);
        // select
        List<String> fields = CollectionUtil.newArrayList(request.getField(), "time");
        // where
        List<WhereCondition> conditions = new ArrayList<>();
        conditions.add(WhereCondition.builder().
                field("biz_device_id").
                compareKeyWord(SqlKeyword.EQ).
                value(bizDeviceId).
                build());
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LE).
                value(formatToUtc(request.getTime())).
                build());
        // order
        String order = "time desc";
        StringBuilder deviceIdBuilder = new StringBuilder();
        List<JSONObject> list = influxdbTemplate.query(fields, measurement, conditions, null, order, 1L, null);
        String value = list != null && list.size() > 0 ? list.get(0).getStr(request.getField()) : "";
        return Response.success(value);
    }

    @Override
    public Response<List<DeviceHistoryDTO>> getDeviceHistory(HistoryQueryInnerDTO queryDTO) {
        String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(queryDTO.getBizProductId());
        String[] codes = queryDTO.getAttrCode().split(StrUtil.COMMA);
        List<String> fields = new ArrayList<>();
        for (String code : codes) {
            if (queryDTO.getPeriodType() == PeriodTypeConst.DEFAULT_PERIOD.getType()) {
                fields.add(code);
            } else {
                fields.add("last(" + code + ") as " + code);
            }
        }

        List<WhereCondition> conds = new ArrayList<WhereCondition>();
        conds.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LE).
                value(DateFormatUtils.formatUTC(Date.from(LocalDateTime.parse(queryDTO.getEndTime(), dateTimeFormatter).
                        atZone(ZoneId.systemDefault()).
                        toInstant()), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern())).
                build());
        conds.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.GE).
                value(DateFormatUtils.formatUTC(Date.from(LocalDateTime.parse(queryDTO.getStartTime(), dateTimeFormatter).
                        atZone(ZoneId.systemDefault()).
                        toInstant()), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern())).
                build());

        StringBuilder deviceIdBuilder = new StringBuilder();
        String[] bizDeviceIds = queryDTO.getBizDeviceIds().split(StrUtil.COMMA);
        if (1 == bizDeviceIds.length) {
            deviceIdBuilder.append(bizDeviceIds[0]);
        } else {
            deviceIdBuilder.append("~/^");
            deviceIdBuilder.append(bizDeviceIds[0]);
            for (int i = 1; i < bizDeviceIds.length; i++) {
                deviceIdBuilder.append("|").append(bizDeviceIds[i]);
            }
            deviceIdBuilder.append("$/");
        }
        conds.add(WhereCondition.builder().
                field("biz_device_id").
                compareKeyWord(SqlKeyword.EQ).
                value(deviceIdBuilder.toString()).
                build());
        String groupBy = "biz_device_id";
        if (queryDTO.getPeriodType() == PeriodTypeConst.DEFAULT_PERIOD.getType()) {
            // 原始的查询时间范围是一周
        } else if (queryDTO.getPeriodType() == PeriodTypeConst.FIVE_MINUTES.getType()) {
            // 5分钟、10分钟、30分钟查询的时间范围是1个月
            groupBy += ",time(5m) fill(null)";
        } else if (queryDTO.getPeriodType() == PeriodTypeConst.TEN_MINUTES.getType()) {
            // 5分钟、10分钟、30分钟查询的时间范围是1个月
            groupBy += ",time(10m) fill(null)";
        } else if (queryDTO.getPeriodType() == PeriodTypeConst.THIRTY_MINUTES.getType()) {
            // 5分钟、10分钟、30分钟查询的时间范围是1个月
            groupBy += ",time(30m) fill(null)";
        } else if (queryDTO.getPeriodType() == PeriodTypeConst.ONE_HOUR.getType()) {
            // 1小时、8小时，1天，查询时间范围是1年
            groupBy += ",time(1h) fill(null)";
        } else if (queryDTO.getPeriodType() == PeriodTypeConst.EIGHT_HOURS.getType()) {
            // 1小时、8小时，1天，查询时间范围是1年
            groupBy += ",time(8h) fill(null)";
        } else if (queryDTO.getPeriodType() == PeriodTypeConst.ONE_DAY.getType()) {
            // 1小时、8小时，1天，查询时间范围是1年
            groupBy += ",time(1d, -8h) fill(null)";
        }

        List<JSONObject> list = influxdbTemplate.query(fields, measurement, conds, groupBy, null, null, null);
        // 拼装属性，为null则不处理
        List<DeviceHistoryDTO> result = new ArrayList<>();
        Map<String, DeviceHistoryDTO> map = Maps.newHashMap();
        if (CollectionUtil.isEmpty(list)) {
            return Response.success(result);
        }
        JSONObject temp;
        String tempKey;
        for (int i = 0; i < list.size(); i++) {
            temp = list.get(i);
            for (String code : codes) {
                tempKey = temp.get("biz_device_id") + code;
                if (!map.containsKey(tempKey)) {
                    map.put(tempKey, new DeviceHistoryDTO());
                    map.get(tempKey).setBizDeviceId(temp.getStr("biz_device_id"));
                    map.get(tempKey).setAttrCode(code);
                    map.get(tempKey).setTimes(new ArrayList<>());
                    map.get(tempKey).setValues(new ArrayList<>());
                }
                // add to list
                map.get(tempKey).getTimes().add(parseIsoTime2Usual(temp.getStr("time"), queryDTO.getPeriodType()));
                map.get(tempKey).getValues().add(temp.getStr(code));
            }
        }
        result = map.values().stream().toList();
        return Response.success(result);
    }

    @Override
    public Response<List<StaDeviceElectricityResponse>> getStaElectricityDeviceData(StaDeviceBaseRequest request) {
        List<StaDeviceElectricityResponse> result = new ArrayList<>();
        request.getProductDeviceIds().forEach((bizProductId, bizDeviceIdList) -> {
            // 表
            String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProductId);
            // select
            List<String> fields = CollectionUtil.newArrayList("Epimp", "Epexp", "biz_device_id");
            // where
            List<WhereCondition> conditions = new ArrayList<>();
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            // 缩小时间范围 增加执行效率
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());

            StringBuilder deviceIdBuilder = new StringBuilder();
            if (1 == bizDeviceIdList.size()) {
                deviceIdBuilder.append(bizDeviceIdList.get(0));
            } else {
                deviceIdBuilder.append("~/^");
                deviceIdBuilder.append(bizDeviceIdList.get(0));
                for (int i = 1; i < bizDeviceIdList.size(); i++) {
                    deviceIdBuilder.append("|").append(bizDeviceIdList.get(i));
                }
                deviceIdBuilder.append("$/");
            }
            WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                    field("biz_device_id").
                    compareKeyWord(SqlKeyword.EQ).
                    value(deviceIdBuilder.toString()).
                    build();
            conditions.add(bizDeviceIdCondition);
            // groupBy
            String groupBy = "biz_device_id";
            // order
            String order = "time desc";
            List<JSONObject> endDataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null);

            conditions.clear();
            // where
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getStart())).
                    build());
            // 20231102 按产品要求 设备小时电量移除开始时间往前追述一天的限制
            // 缩小时间范围 增加执行效率
//            conditions.add(WhereCondition.builder().
//                    field("time").
//                    compareKeyWord(SqlKeyword.GE).
//                    value(formatToUtc(request.getStart().minusDays(1L))).
//                    build());
            conditions.add(bizDeviceIdCondition);
            List<JSONObject> startDataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null);
            for (String bizDeviceId : bizDeviceIdList) {
                StaDeviceElectricityResponse data = new StaDeviceElectricityResponse();
                Optional<JSONObject> endDataOptional = endDataList.stream().filter(item -> item.getStr("biz_device_id").equals(bizDeviceId)).findAny();
                if (endDataOptional.isPresent()) {
                    JSONObject endData = endDataOptional.get();
                    data.setReEndData(endData.getBigDecimal("Epexp"))
                            .setEndData(endData.getBigDecimal("Epimp"));

                }
                Optional<JSONObject> startDataOptional = startDataList.stream().filter(item -> item.getStr("biz_device_id").equals(bizDeviceId)).findAny();
                if (startDataOptional.isPresent()) {
                    JSONObject startData = startDataOptional.get();
                    data.setStartData(startData.getBigDecimal("Epimp"))
                            .setReStartData(startData.getBigDecimal("Epexp"));
                }

                data.setBizProductId(bizProductId)
                        .setBizDeviceId(bizDeviceId)
                ;
                result.add(data);
            }
        });
        return Response.success(result);
    }

    @Override
    public Response<List<StaDeviceGasResponse>> getStaGasDeviceData(StaDeviceBaseRequest request) {
        List<StaDeviceGasResponse> result = new ArrayList<>();
        request.getProductDeviceIds().forEach((bizProductId, bizDeviceIdList) -> {
            // 表
            String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProductId);
            // select
            List<String> fields = CollectionUtil.newArrayList("Gascons", "biz_device_id");
            // where
            List<WhereCondition> conditions = new ArrayList<>();
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            // 缩小时间范围 增加执行效率
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());

            StringBuilder deviceIdBuilder = new StringBuilder();
            if (1 == bizDeviceIdList.size()) {
                deviceIdBuilder.append(bizDeviceIdList.get(0));
            } else {
                deviceIdBuilder.append("~/^");
                deviceIdBuilder.append(bizDeviceIdList.get(0));
                for (int i = 1; i < bizDeviceIdList.size(); i++) {
                    deviceIdBuilder.append("|").append(bizDeviceIdList.get(i));
                }
                deviceIdBuilder.append("$/");
            }
            WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                    field("biz_device_id").
                    compareKeyWord(SqlKeyword.EQ).
                    value(deviceIdBuilder.toString()).
                    build();
            conditions.add(bizDeviceIdCondition);
            // groupBy
            String groupBy = "biz_device_id";
            // order
            String order = "time desc";
            Map<String, BigDecimal> endDataMap = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null)
                    .stream()
                    .collect(Collectors.toMap(o -> o.getStr("biz_device_id"), o -> o.getBigDecimal("Gascons")));
            conditions.clear();
            // where
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getStart())).
                    build());
            // 缩小时间范围 增加执行效率
//            conditions.add(WhereCondition.builder().
//                    field("time").
//                    compareKeyWord(SqlKeyword.GE).
//                    value(formatToUtc(request.getStart().minusDays(1L))).
//                    build());
            conditions.add(bizDeviceIdCondition);
            Map<String, BigDecimal> startDataMap = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null)
                    .stream()
                    .collect(Collectors.toMap(o -> o.getStr("biz_device_id"), o -> o.getBigDecimal("Gascons")));
            for (String bizDeviceId : bizDeviceIdList) {
                StaDeviceGasResponse data = new StaDeviceGasResponse();
                data.setBizProductId(bizProductId)
                        .setBizDeviceId(bizDeviceId)
                        .setStartData(startDataMap.get(bizDeviceId))
                        .setEndData(endDataMap.get(bizDeviceId));
                result.add(data);
            }
        });
        return Response.success(result);
    }


    @Override
    public Response<List<StaDeviceWaterResponse>> getStaWaterDeviceData(StaDeviceBaseRequest request) {
        List<StaDeviceWaterResponse> result = new ArrayList<>();
        request.getProductDeviceIds().forEach((bizProductId, bizDeviceIdList) -> {
            // 表
            String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProductId);
            // select
            List<String> fields = CollectionUtil.newArrayList("Watercons", "biz_device_id");
            // where
            List<WhereCondition> conditions = new ArrayList<>();
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            // 缩小时间范围 增加执行效率
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());

            StringBuilder deviceIdBuilder = new StringBuilder();
            if (1 == bizDeviceIdList.size()) {
                deviceIdBuilder.append(bizDeviceIdList.get(0));
            } else {
                deviceIdBuilder.append("~/^");
                deviceIdBuilder.append(bizDeviceIdList.get(0));
                for (int i = 1; i < bizDeviceIdList.size(); i++) {
                    deviceIdBuilder.append("|").append(bizDeviceIdList.get(i));
                }
                deviceIdBuilder.append("$/");
            }
            WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                    field("biz_device_id").
                    compareKeyWord(SqlKeyword.EQ).
                    value(deviceIdBuilder.toString()).
                    build();
            conditions.add(bizDeviceIdCondition);
            // groupBy
            String groupBy = "biz_device_id";
            // order
            String order = "time desc";
            Map<String, BigDecimal> endDataMap = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null)
                    .stream()
                    .collect(Collectors.toMap(o -> o.getStr("biz_device_id"), o -> o.getBigDecimal("Watercons")));
            conditions.clear();
            // where
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getStart())).
                    build());
            // 缩小时间范围 增加执行效率
//            conditions.add(WhereCondition.builder().
//                    field("time").
//                    compareKeyWord(SqlKeyword.GE).
//                    value(formatToUtc(request.getStart().minusDays(1L))).
//                    build());
            conditions.add(bizDeviceIdCondition);
            Map<String, BigDecimal> startDataMap = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null)
                    .stream()
                    .collect(Collectors.toMap(o -> o.getStr("biz_device_id"), o -> o.getBigDecimal("Watercons")));
            for (String bizDeviceId : bizDeviceIdList) {
                StaDeviceWaterResponse data = new StaDeviceWaterResponse();
                data.setBizProductId(bizProductId)
                        .setBizDeviceId(bizDeviceId)
                        .setStartData(startDataMap.get(bizDeviceId))
                        .setEndData(endDataMap.get(bizDeviceId));
                result.add(data);
            }
        });
        return Response.success(result);
    }

    @Override
    public Response<Collection<StaDeviceAirResponse>> getStaAirDeviceData(StaDeviceBaseRequest request) {
        Map<String, StaDeviceAirResponse> resultMap = new HashMap<>(64);
        request.getProductDeviceIds().forEach((bizProductId, bizDeviceIdList) -> {
            // 表
            String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProductId);
            // select
            List<String> fields = CollectionUtil.newArrayList("CST", "RST", "Temperature", "biz_device_id");
            // where 补偿查询整点的数据
            List<WhereCondition> conditions = new ArrayList<>();
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getStart())).
                    build());
//            conditions.add(WhereCondition.builder().
//                    field("time").
//                    compareKeyWord(SqlKeyword.GE).
//                    value(formatToUtc(request.getStart().minusDays(1L))).
//                    build());
            StringBuilder deviceIdBuilder = new StringBuilder();
            if (1 == bizDeviceIdList.size()) {
                deviceIdBuilder.append(bizDeviceIdList.get(0));
            } else {
                deviceIdBuilder.append("~/^");
                deviceIdBuilder.append(bizDeviceIdList.get(0));
                for (int i = 1; i < bizDeviceIdList.size(); i++) {
                    deviceIdBuilder.append("|").append(bizDeviceIdList.get(i));
                }
                deviceIdBuilder.append("$/");
            }
            WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                    field("biz_device_id").
                    compareKeyWord(SqlKeyword.EQ).
                    value(deviceIdBuilder.toString()).
                    build();
            conditions.add(bizDeviceIdCondition);
            // groupBy
            String groupBy = "biz_device_id";
            // order
            String order = "time desc";
            Map<String, JSONObject> firstDataMap = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null)
                    .stream()
                    .collect(Collectors.toMap(o -> o.getStr("biz_device_id"), o -> o));
            conditions.clear();
            // where 正常小时内的原始数据
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());
            conditions.add(bizDeviceIdCondition);
            order = "time asc";
            influxdbTemplate.query(fields, measurement, conditions, groupBy, order, null, null)
                    .forEach(o -> {
                        String bizDeviceId = o.getStr("biz_device_id");
                        StaDeviceAirResponse data = resultMap.get(bizDeviceId);
                        if (null == data) {
                            data = new StaDeviceAirResponse()
                                    .setBizDeviceId(bizDeviceId)
                                    .setCstDataList(new ArrayList<>())
                                    .setRstDataList(new ArrayList<>())
                                    .setTemperatureDataList(new ArrayList<>());
                            resultMap.put(bizDeviceId, data);
                            // 补偿处理整点开始的数据（为了精确时间区间）
                            if (paresToLocal(o.getStr("time")).compareTo(request.getStart()) > 0) {
                                JSONObject firstData = firstDataMap.get(bizDeviceId);
                                if (null != firstData) {
                                    Integer cst = firstData.getInt("CST");
                                    if (null != cst) {
                                        data.getCstDataList().add(new IntAttrValue(request.getStart(), cst));
                                    }
                                    Integer rst = firstData.getInt("RST");
                                    if (null != rst) {
                                        data.getRstDataList().add(new IntAttrValue(request.getStart(), rst));
                                    }
                                }
                            }
                        }
                        LocalDateTime time = paresToLocal(o.getStr("time"));
                        Integer cst = o.getInt("CST");
                        if (null != cst) {
                            data.getCstDataList().add(new IntAttrValue(time, cst));
                        }
                        Integer rst = o.getInt("RST");
                        if (null != rst) {
                            data.getRstDataList().add(new IntAttrValue(time, rst));
                        }
                        BigDecimal temperature = o.getBigDecimal("Temperature");
                        if (null != temperature) {
                            data.getTemperatureDataList().add(new BigDecimalAttrValue(time, temperature));
                        }
                    });
            // 补偿处理结束整点的数据（为了精确时间区间）
            LocalDateTime endTime = request.getEnd();
            resultMap.values().forEach(o -> {
                List<IntAttrValue> cstDataList = o.getCstDataList();
                if (cstDataList.size() > 0 && cstDataList.get(cstDataList.size() - 1).time().compareTo(endTime) < 0) {
                    cstDataList.add(new IntAttrValue(endTime, cstDataList.get(cstDataList.size() - 1).value()));
                }
                List<IntAttrValue> rstDataList = o.getRstDataList();
                if (rstDataList.size() > 0 && rstDataList.get(rstDataList.size() - 1).time().compareTo(endTime) < 0) {
                    rstDataList.add(new IntAttrValue(endTime, rstDataList.get(rstDataList.size() - 1).value()));
                }
            });
        });
        return Response.success(resultMap.values());
    }

    @Override
    public Response<List<StaDeviceZnbResponse>> getStaZnbDeviceData(StaDeviceBaseRequest request) {
        Map<String, StaDeviceZnbResponse> resultMap = new HashMap<>(64);
        request.getProductDeviceIds().forEach((bizProductId, bizDeviceIdList) -> {
            // 表
            String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProductId);
            // select
            List<String> fields = CollectionUtil.newArrayList("CST", "znbRST", "Epexp", "Epimp", "Eqexp", "Eqimp", "P", "biz_device_id");
            // where
            List<WhereCondition> conditions = new ArrayList<>();
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            // 缩小时间范围 增加执行效率
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());

            StringBuilder deviceIdBuilder = new StringBuilder();
            if (1 == bizDeviceIdList.size()) {
                deviceIdBuilder.append(bizDeviceIdList.get(0));
            } else {
                deviceIdBuilder.append("~/^");
                deviceIdBuilder.append(bizDeviceIdList.get(0));
                for (int i = 1; i < bizDeviceIdList.size(); i++) {
                    deviceIdBuilder.append("|").append(bizDeviceIdList.get(i));
                }
                deviceIdBuilder.append("$/");
            }
            WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                    field("biz_device_id").
                    compareKeyWord(SqlKeyword.EQ).
                    value(deviceIdBuilder.toString()).
                    build();
            conditions.add(bizDeviceIdCondition);
            // groupBy
            String groupBy = "biz_device_id";
            // order
            String order = "time desc";
            List<JSONObject> endDataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null);
            List<JSONObject> pMaxDataList = influxdbTemplate.query(Arrays.asList("MAX(P) as P", "biz_device_id"), measurement, conditions, groupBy, order, null, null);
            Map<String, JSONObject> firstDataMap = endDataList.stream()
                    .collect(Collectors.toMap(o -> o.getStr("biz_device_id"), o -> o));
            Map<String, BigDecimal> pMaxData = pMaxDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("P")),
                            HashMap::putAll);
            Map<String, BigDecimal> epimpEndData = endDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epimp")),
                            HashMap::putAll);
            Map<String, BigDecimal> epexpEndData = endDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epexp")),
                            HashMap::putAll);
            Map<String, BigDecimal> eqimpEndData = endDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Eqimp")),
                            HashMap::putAll);
            Map<String, BigDecimal> eqexpEndData = endDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Eqexp")),
                            HashMap::putAll);
            conditions.clear();
            // where
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getStart())).
                    build());
            // 缩小时间范围 增加执行效率
//            conditions.add(WhereCondition.builder().
//                    field("time").
//                    compareKeyWord(SqlKeyword.GE).
//                    value(formatToUtc(request.getStart().minusDays(1L))).
//                    build());
            conditions.add(bizDeviceIdCondition);
            List<JSONObject> startDataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null);
            Map<String, BigDecimal> epimpStartData = startDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epimp")),
                            HashMap::putAll);
            Map<String, BigDecimal> epexpStartData = startDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epexp")),
                            HashMap::putAll);
            Map<String, BigDecimal> eqimpStartData = startDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Eqimp")),
                            HashMap::putAll);
            Map<String, BigDecimal> eqexpStartData = startDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Eqexp")),
                            HashMap::putAll);
            conditions.clear();
            for (String bizDeviceId : bizDeviceIdList) {
                StaDeviceZnbResponse data = new StaDeviceZnbResponse();
                data.setBizProductId(bizProductId)
                        .setBizDeviceId(bizDeviceId)
                        .setPMax(pMaxData.get(bizDeviceId))
                        .setEpimpEndData(epimpEndData.get(bizDeviceId))
                        .setEpimpStartData(epimpStartData.get(bizDeviceId))
                        .setEpexpEndData(epexpEndData.get(bizDeviceId))
                        .setEpexpStartData(epexpStartData.get(bizDeviceId))
                        .setEqimpEndData(eqimpEndData.get(bizDeviceId))
                        .setEqimpStartData(eqimpStartData.get(bizDeviceId))
                        .setEqexpEndData(eqexpEndData.get(bizDeviceId))
                        .setEqexpStartData(eqexpStartData.get(bizDeviceId));
                resultMap.put(bizDeviceId, data);
            }
            // ********** 处理 CST RST **********
            conditions.clear();
            // where 正常小时内的原始数据
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());
            conditions.add(bizDeviceIdCondition);
            order = "time asc";
            influxdbTemplate.query(fields, measurement, conditions, groupBy, order, null, null)
                    .forEach(o -> {
                        String bizDeviceId = o.getStr("biz_device_id");
                        StaDeviceZnbResponse data = resultMap.get(bizDeviceId);
                        if (null == data) {
                            data = new StaDeviceZnbResponse()
                                    .setBizDeviceId(bizDeviceId)
                                    .setCstDataList(new ArrayList<>())
                                    .setRstDataList(new ArrayList<>());
                            resultMap.put(bizDeviceId, data);
                            // 补偿处理整点开始的数据（为了精确时间区间）
                            if (paresToLocal(o.getStr("time")).compareTo(request.getStart()) > 0) {
                                JSONObject firstData = firstDataMap.get(bizDeviceId);
                                if (null != firstData) {
                                    Integer cst = firstData.getInt("CST");
                                    if (null != cst) {
                                        data.getCstDataList().add(new IntAttrValue(request.getStart(), cst));
                                    }
                                    Integer rst = firstData.getInt("znbRST");
                                    if (null != rst) {
                                        data.getRstDataList().add(new IntAttrValue(request.getStart(), rst));
                                    }
                                }
                            }
                        }
                        LocalDateTime time = paresToLocal(o.getStr("time"));
                        Integer cst = o.getInt("CST");
                        if (null != cst) {
                            data.getCstDataList().add(new IntAttrValue(time, cst));
                        }
                        Integer rst = o.getInt("znbRST");
                        if (null != rst) {
                            data.getRstDataList().add(new IntAttrValue(time, rst));
                        }
                    });
            // 补偿处理结束整点的数据（为了精确时间区间）
            LocalDateTime endTime = request.getEnd();
            resultMap.values().forEach(o -> {
                List<IntAttrValue> cstDataList = o.getCstDataList();
                if (cstDataList.size() > 0 && cstDataList.get(cstDataList.size() - 1).time().compareTo(endTime) < 0) {
                    cstDataList.add(new IntAttrValue(endTime, cstDataList.get(cstDataList.size() - 1).value()));
                }
                List<IntAttrValue> rstDataList = o.getRstDataList();
                if (rstDataList.size() > 0 && rstDataList.get(rstDataList.size() - 1).time().compareTo(endTime) < 0) {
                    rstDataList.add(new IntAttrValue(endTime, rstDataList.get(rstDataList.size() - 1).value()));
                }
            });
        });

        return Response.success(resultMap.values().stream().toList());
    }

    @Override
    public Response<List<StaDeviceGscnResponse>> getStaGscnDeviceData(StaDeviceBaseRequest request) {
        Map<String, StaDeviceGscnResponse> resultMap = new HashMap<>(64);
        request.getProductDeviceIds().forEach((bizProductId, bizDeviceIdList) -> {
            // 表
            String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(bizProductId);
            // select
            List<String> fields = CollectionUtil.newArrayList("CST", "pcsRST", "Epexp", "Epimp", "biz_device_id");
            // where
            List<WhereCondition> conditions = new ArrayList<>();
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            // 缩小时间范围 增加执行效率
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());

            StringBuilder deviceIdBuilder = new StringBuilder();
            if (1 == bizDeviceIdList.size()) {
                deviceIdBuilder.append(bizDeviceIdList.get(0));
            } else {
                deviceIdBuilder.append("~/^");
                deviceIdBuilder.append(bizDeviceIdList.get(0));
                for (int i = 1; i < bizDeviceIdList.size(); i++) {
                    deviceIdBuilder.append("|").append(bizDeviceIdList.get(i));
                }
                deviceIdBuilder.append("$/");
            }
            WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                    field("biz_device_id").
                    compareKeyWord(SqlKeyword.EQ).
                    value(deviceIdBuilder.toString()).
                    build();
            conditions.add(bizDeviceIdCondition);
            // groupBy
            String groupBy = "biz_device_id";
            // order
            String order = "time desc";
            List<JSONObject> endDataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null);
            Map<String, JSONObject> firstDataMap = endDataList.stream()
                    .collect(Collectors.toMap(o -> o.getStr("biz_device_id"), o -> o));
            Map<String, BigDecimal> epimpEndData = endDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epimp")),
                            HashMap::putAll);
            Map<String, BigDecimal> epexpEndData = endDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epexp")),
                            HashMap::putAll);
            conditions.clear();
            // where
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getStart())).
                    build());
            // 缩小时间范围 增加执行效率
//            conditions.add(WhereCondition.builder().
//                    field("time").
//                    compareKeyWord(SqlKeyword.GE).
//                    value(formatToUtc(request.getStart().minusDays(1L))).
//                    build());
            conditions.add(bizDeviceIdCondition);
            List<JSONObject> startDataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, order, 1L, null);
            Map<String, BigDecimal> epimpStartData = startDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epimp")),
                            HashMap::putAll);
            Map<String, BigDecimal> epexpStartData = startDataList
                    .stream()
                    .collect(HashMap::new,
                            (map, item) -> map.put(item.getStr("biz_device_id"), item.getBigDecimal("Epexp")),
                            HashMap::putAll);
            conditions.clear();
            for (String bizDeviceId : bizDeviceIdList) {
                StaDeviceGscnResponse data = new StaDeviceGscnResponse();
                data.setBizProductId(bizProductId)
                        .setBizDeviceId(bizDeviceId)
                        .setEpimpEndData(epimpEndData.get(bizDeviceId))
                        .setEpimpStartData(epimpStartData.get(bizDeviceId))
                        .setEpexpEndData(epexpEndData.get(bizDeviceId))
                        .setEpexpStartData(epexpStartData.get(bizDeviceId));
                resultMap.put(bizDeviceId, data);
            }
            // ********** 处理 CST RST **********
            conditions.clear();
            // where 正常小时内的原始数据
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.LE).
                    value(formatToUtc(request.getEnd())).
                    build());
            conditions.add(WhereCondition.builder().
                    field("time").
                    compareKeyWord(SqlKeyword.GE).
                    value(formatToUtc(request.getStart())).
                    build());
            conditions.add(bizDeviceIdCondition);
            order = "time asc";
            influxdbTemplate.query(fields, measurement, conditions, groupBy, order, null, null)
                    .forEach(o -> {
                        String bizDeviceId = o.getStr("biz_device_id");
                        StaDeviceGscnResponse data = resultMap.get(bizDeviceId);
                        if (null == data) {
                            data = new StaDeviceGscnResponse()
                                    .setBizDeviceId(bizDeviceId)
                                    .setCstDataList(new ArrayList<>())
                                    .setPcsrstDataList(new ArrayList<>());
                            resultMap.put(bizDeviceId, data);
                            // 补偿处理整点开始的数据（为了精确时间区间）
                            if (paresToLocal(o.getStr("time")).compareTo(request.getStart()) > 0) {
                                JSONObject firstData = firstDataMap.get(bizDeviceId);
                                if (null != firstData) {
                                    Integer cst = firstData.getInt("CST");
                                    if (null != cst) {
                                        data.getCstDataList().add(new IntAttrValue(request.getStart(), cst));
                                    }
                                    Integer rst = firstData.getInt("pcsRST");
                                    if (null != rst) {
                                        data.getPcsrstDataList().add(new IntAttrValue(request.getStart(), rst));
                                    }
                                }
                            }
                        }
                        LocalDateTime time = paresToLocal(o.getStr("time"));
                        Integer cst = o.getInt("CST");
                        if (null != cst) {
                            data.getCstDataList().add(new IntAttrValue(time, cst));
                        }
                        Integer rst = o.getInt("pcsRST");
                        if (null != rst) {
                            data.getPcsrstDataList().add(new IntAttrValue(time, rst));
                        }
                    });
            // 补偿处理结束整点的数据（为了精确时间区间）
            LocalDateTime endTime = request.getEnd();
            resultMap.values().forEach(o -> {
                List<IntAttrValue> cstDataList = o.getCstDataList();
                if (cstDataList.size() > 0 && cstDataList.get(cstDataList.size() - 1).time().compareTo(endTime) < 0) {
                    cstDataList.add(new IntAttrValue(endTime, cstDataList.get(cstDataList.size() - 1).value()));
                }
                List<IntAttrValue> rstDataList = o.getPcsrstDataList();
                if (rstDataList.size() > 0 && rstDataList.get(rstDataList.size() - 1).time().compareTo(endTime) < 0) {
                    rstDataList.add(new IntAttrValue(endTime, rstDataList.get(rstDataList.size() - 1).value()));
                }
            });
        });

        return Response.success(resultMap.values().stream().toList());
    }

    @Override
    public Response<List<ZnbPResponse>> getZnbPResponse(BasePRequest request) {
        List<String> deviceIds = request.getDeviceIds();
        // 1、根据设备id查询产品业务id
        // 2、拼成表名
        String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(request.getProductBizId());
        // select
        List<String> fields = CollectionUtil.newArrayList("LAST(\"P\")");
        // where
        List<WhereCondition> conditions = new ArrayList<>();
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LE).
                value(formatToUtc(request.getEnd())).
                build());
        // 缩小时间范围 增加执行效率
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.GE).
                value(formatToUtc(request.getStart())).
                build());

        StringBuilder deviceIdBuilder = new StringBuilder();
        if (1 == deviceIds.size()) {
            deviceIdBuilder.append(deviceIds.get(0));
        } else {
            deviceIdBuilder.append("~/^");
            deviceIdBuilder.append(deviceIds.get(0));
            for (int i = 1; i < deviceIds.size(); i++) {
                deviceIdBuilder.append("|").append(deviceIds.get(i));
            }
            deviceIdBuilder.append("$/");
        }
        WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                field("biz_device_id").
                compareKeyWord(SqlKeyword.EQ).
                value(deviceIdBuilder.toString()).
                build();
        conditions.add(bizDeviceIdCondition);
        // groupBy
        String groupBy = "time(5m)";
        List<JSONObject> dataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, null, null, null);

        List<ZnbPResponse> znbPRespons = new ArrayList<>();
        for (JSONObject entries : dataList) {
            String time = entries.getStr("time");
            BigDecimal p = entries.getBigDecimal("last");
            ZnbPResponse znbPResponse = new ZnbPResponse();
            znbPResponse.setTime(time);
            znbPResponse.setP(p);
            znbPRespons.add(znbPResponse);
        }
        return Response.success(znbPRespons);
    }

    @Override
    public Response<List<GscnPResponse>> getGscnPResponse(BasePRequest request) {
        List<String> deviceIds = request.getDeviceIds();
        // 表
        String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(request.getProductBizId());
        // select
        List<String> fields = CollectionUtil.newArrayList("LAST(\"P\")");
        // where
        List<WhereCondition> conditions = new ArrayList<>();
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LE).
                value(formatToUtc(request.getEnd())).
                build());
        // 缩小时间范围 增加执行效率
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.GE).
                value(formatToUtc(request.getStart())).
                build());

        StringBuilder deviceIdBuilder = new StringBuilder();
        if (1 == deviceIds.size()) {
            deviceIdBuilder.append(deviceIds.get(0));
        } else {
            deviceIdBuilder.append("~/^");
            deviceIdBuilder.append(deviceIds.get(0));
            for (int i = 1; i < deviceIds.size(); i++) {
                deviceIdBuilder.append("|").append(deviceIds.get(i));
            }
            deviceIdBuilder.append("$/");
        }
        WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                field("biz_device_id").
                compareKeyWord(SqlKeyword.EQ).
                value(deviceIdBuilder.toString()).
                build();
        conditions.add(bizDeviceIdCondition);
        // groupBy
        String groupBy = "time(5m)";
        List<JSONObject> dataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, null, null, null);

        List<GscnPResponse> gscnPResponses = new ArrayList<>();
        for (JSONObject entries : dataList) {
            String time = entries.getStr("time");
            BigDecimal p = entries.getBigDecimal("last");
            GscnPResponse gscnPResponse = new GscnPResponse();
            gscnPResponse.setTime(time);
            gscnPResponse.setP(p);
            gscnPResponses.add(gscnPResponse);
        }
        return Response.success(gscnPResponses);
    }

    @Override
    public Response<List<ChargePResponse>> getChargePResponse(BasePRequest request) {
        List<String> deviceIds = request.getDeviceIds();
        // 表
        String measurement = MeasurementFindUtil.getDeviceStatusMeasurementByProdCode(request.getProductBizId());
        // select
        List<String> fields = CollectionUtil.newArrayList("LAST(\"P\")");
        // where
        List<WhereCondition> conditions = new ArrayList<>();
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.LE).
                value(formatToUtc(request.getEnd())).
                build());
        // 缩小时间范围 增加执行效率
        conditions.add(WhereCondition.builder().
                field("time").
                compareKeyWord(SqlKeyword.GE).
                value(formatToUtc(request.getStart())).
                build());

        StringBuilder deviceIdBuilder = new StringBuilder();
        if (1 == deviceIds.size()) {
            deviceIdBuilder.append(deviceIds.get(0));
        } else {
            deviceIdBuilder.append("~/^");
            deviceIdBuilder.append(deviceIds.get(0));
            for (int i = 1; i < deviceIds.size(); i++) {
                deviceIdBuilder.append("|").append(deviceIds.get(i));
            }
            deviceIdBuilder.append("$/");
        }
        WhereCondition bizDeviceIdCondition = WhereCondition.builder().
                field("biz_device_id").
                compareKeyWord(SqlKeyword.EQ).
                value(deviceIdBuilder.toString()).
                build();
        conditions.add(bizDeviceIdCondition);
        // groupBy
        String groupBy = "time(5m)";
        List<JSONObject> dataList = influxdbTemplate.query(fields, measurement, conditions, groupBy, null, null, null);

        List<ChargePResponse> chargePResponses = new ArrayList<>();
        for (JSONObject entries : dataList) {
            String time = entries.getStr("time");
            BigDecimal p = entries.getBigDecimal("last");
            ChargePResponse chargePResponse = new ChargePResponse();
            chargePResponse.setTime(time);
            chargePResponse.setP(p);
            chargePResponses.add(chargePResponse);
        }
        return Response.success(chargePResponses);
    }

    private String parseIsoTime2Usual(String utcTime, Integer queryType) {
        // modify by hebin.
        // there is an issue when use group by time function in influx 1.x
        // cause when use it, the range of result is the time range [time, time+1], not the [time-1,time], means u would get a older time but a further data
        // so this function make the time add the during to fix it.
        LocalDateTime time = LocalDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        if (null == queryType || queryType == PeriodTypeConst.DEFAULT_PERIOD.getType()) {
            // 原始的查询时间范围是一周
        } else if (queryType == PeriodTypeConst.FIVE_MINUTES.getType()) {
            // 5分钟、10分钟、30分钟查询的时间范围是1个月
            time = time.plusMinutes(5L);
        } else if (queryType == PeriodTypeConst.TEN_MINUTES.getType()) {
            // 5分钟、10分钟、30分钟查询的时间范围是1个月
            time = time.plusMinutes(10L);
        } else if (queryType == PeriodTypeConst.THIRTY_MINUTES.getType()) {
            // 5分钟、10分钟、30分钟查询的时间范围是1个月
            time = time.plusMinutes(30L);
        } else if (queryType == PeriodTypeConst.ONE_HOUR.getType()) {
            // 1小时、8小时，1天，查询时间范围是1年
            time = time.plusHours(1L);
        } else if (queryType == PeriodTypeConst.EIGHT_HOURS.getType()) {
            // 1小时、8小时，1天，查询时间范围是1年
            time = time.plusHours(8L);
        } else if (queryType == PeriodTypeConst.ONE_DAY.getType()) {
            // 1小时、8小时，1天，查询时间范围是1年
        }
        return time.format(dateTimeFormatter);
    }

    private String formatToUtc(LocalDateTime time) {
        return DateFormatUtils.formatUTC(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern());
    }

    private LocalDateTime paresToLocal(String utcTime) {
        return LocalDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

}
