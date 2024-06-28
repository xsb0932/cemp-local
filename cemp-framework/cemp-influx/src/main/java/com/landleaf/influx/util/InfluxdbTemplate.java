package com.landleaf.influx.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import com.landleaf.influx.conditions.QueryBuilder;
import com.landleaf.influx.conf.InfluxdbProperties;
import com.landleaf.influx.model.TimeColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author eason
 */
@Slf4j
@Component
public class InfluxdbTemplate {
    @Autowired
    private InfluxDBClient client;
    @Autowired
    private InfluxdbProperties influxdbProperties;

    /**
     * 单个历史数据写入
     */
    public <T extends TimeColumn> void write(T data) {
        try (WriteApi writeApi = client.makeWriteApi()) {
            writeApi.writeMeasurement(WritePrecision.NS, data);
        } catch (Exception e) {
            log.error("influx写入异常 data:{}", data, e);
        }
    }

    /**
     * 批量历史数据写入
     */
    public <T extends TimeColumn> void batchWrite(List<T> data) {
        log.info("历史数据批量保存 size:{}", CollectionUtil.size(data));
        try (WriteApi writeApi = client.makeWriteApi()) {
            writeApi.writeMeasurements(WritePrecision.NS, data);
        } catch (Exception e) {
            log.error("influx批量写入异常 data:{}", data, e);
        }
    }


    /**
     * 获取时间点往前最新的一条
     */
    public <T extends TimeColumn> T queryLast(QueryBuilder<T> builder) {
        // bucket
        String fluxQuery = "from(bucket: \"" + influxdbProperties.getBucket() + "\")\n" +
                // range
                builder.getRangeFlux() +
                // measurement + filter
                builder.getFilterFlux() +
                // last one
                " |> last()\n" +
                // pivot
                " |> pivot( rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\" )";
        log.info("influx query:\n{}", fluxQuery);
        List<T> list = query(fluxQuery, builder.getEntityClass());
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 根据查询条件获取历史数据List
     */
    public <T extends TimeColumn> List<T> queryHistory(QueryBuilder<T> builder) {
        // bucket
        String fluxQuery = "from(bucket: \"" + influxdbProperties.getBucket() + "\")\n" +
                // range
                builder.getRangeFlux() +
                // measurement + filter
                builder.getFilterFlux() +
                // pivot
                " |> pivot( rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\" )";
        log.info("influx query:\n{}", fluxQuery);
        return query(fluxQuery, builder.getEntityClass());
    }

    public List<FluxTable> query(String flux) {
        return client.getQueryApi().query(flux);
    }

    public <T> List<T> query(String flux, Class<T> c) {
        try {
            return client.getQueryApi().query(flux, c);
        } catch (Exception e) {
            log.error("influx query error", e);
        }
        return CollectionUtil.empty(c);
    }

    /**
     * OffsetDateTime start = OffsetDateTime.now().minus(7, ChronoUnit.DAYS);
     * OffsetDateTime stop = OffsetDateTime.now();
     * "_measurement=\"device_status_history\""
     *
     * @param start
     * @param stop
     * @param predicate
     */
    public void delete(OffsetDateTime start, OffsetDateTime stop, String predicate) {
        DeleteApi deleteApi = client.getDeleteApi();
        try {
            deleteApi.delete(start, stop, predicate, influxdbProperties.getBucket(), influxdbProperties.getOrg());
        } catch (Exception e) {
            log.error("influx删除异常", e);
        }
    }

    private <T extends TimeColumn> Map<String, List<T>> toTimeStepMap(Map<String, List<T>> sourceMap, List<LocalDateTime> timeStep) {
        Map<String, List<T>> result = new HashMap<>(sourceMap.size());
        for (Map.Entry<String, List<T>> entry : sourceMap.entrySet()) {
            List<T> temp = new ArrayList<>(timeStep.size());
            TreeMap<LocalDateTime, T> timeValueMap = entry.getValue().stream().collect(Collectors.toMap(
                    TimeColumn::getLocalTime,
                    o -> o,
                    (o1, o2) -> o1,
                    TreeMap::new
            ));
            for (LocalDateTime time : timeStep) {
                T t = timeValueMap.get(time);
                if (t != null) {
                    temp.add(t);
                } else {
                    LocalDateTime lowerTimeStep = timeValueMap.lowerKey(time);
                    temp.add(null != lowerTimeStep ? timeValueMap.get(lowerTimeStep) : null);
                }
            }
            result.put(entry.getKey(), temp);
        }
        return result;
    }

    public List<LocalDateTime> getTimeStep(LocalDateTime start, LocalDateTime end, Long step, ChronoUnit unit) {
        List<LocalDateTime> result = Lists.newArrayList();
        LocalDateTime temp = start;
        do {
            result.add(temp);
            temp = temp.plus(step, unit);
        } while (temp.compareTo(end) <= 0);
        return result;
    }
}
