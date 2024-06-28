package com.landleaf.influx.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.landleaf.influx.condition.WhereCondition;
import com.landleaf.influx.enums.SqlKeyword;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lokiy
 * @date 2021/11/25
 * @description inlfuxdb模版方法
 */
@Component
@Slf4j
public class InfluxdbTemplate {

    @Resource
    private InfluxDB client;

    //========================================== insert operation ======================================================

    public void insert(Point point) {
        this.client.write(point);
    }

    public void insert(String influxQL) {
        this.client.write(influxQL);
    }

    public void insert(String measurement, long time, Map<String, String> tags, Map<String, Object> fields) {
        Point point = Point.measurement(measurement)
                .time(time, TimeUnit.MILLISECONDS)
                .tag(tags)
                .fields(fields)
                .build();
        this.insert(point);
    }

    public void insert(String measurement, long time, TimeUnit timeUnit, Map<String, String> tags, Map<String, Object> fields) {
        Point point = Point.measurement(measurement)
                .time(time, timeUnit)
                .tag(tags)
                .fields(fields)
                .build();
        this.insert(point);
    }

    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        Point point = Point.measurement(measurement)
                .tag(tags)
                .fields(fields)
                .build();
        this.insert(point);
    }

    public void insertBatch(List<String> influxQLs) {
        this.client.write(influxQLs);
    }

    public void insertBatchV1(List<Point> points) {
        List<String> influxQLs = points.stream().map(Point::lineProtocol).collect(Collectors.toList());
        this.insertBatch(influxQLs);
    }

    public void insertBatchV2(List<Point> points) {
        BatchPoints batchPoints = BatchPoints.builder()
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        points.forEach(batchPoints::point);
        this.client.write(batchPoints);
    }


    //=======================================query operation============================================================

    private QueryResult queryOri(String influxQL) {
        return this.client.query(new Query(influxQL));
    }

    public List<JSONObject> queryBase(String influxQL) {
        log.debug("执行的最终查询语句为---------->{}", influxQL);
        QueryResult qR = this.queryOri(influxQL);
        List<JSONObject> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(qR.getResults())) {
            return Lists.newArrayList();
        }
        List<String> fields;
        Map<String, String> tags;
        for (QueryResult.Result r : qR.getResults()) {
            if (!CollectionUtils.isEmpty(r.getSeries())) {
                for (QueryResult.Series s : r.getSeries()) {
                    //列名
                    fields = s.getColumns();
                    tags = s.getTags();

                    //每一行的值
                    if (!CollectionUtils.isEmpty(s.getValues())) {
                        for (List<Object> value : s.getValues()) {
                            JSONObject temp = JSONUtil.createObj();
                            for (int i = 0; i < fields.size(); i++) {
                                temp.set(fields.get(i), value.get(i));
                            }
                            // modify by hebin， group by的时候，把tag直接塞到result中返回
                            if (!MapUtil.isEmpty(tags)) {
                                tags.forEach((k, v) -> {
                                    temp.set(k, v);
                                });
                            }
                            result.add(temp);
                        }
                    }
                }
            }
        }
        return result;
    }


    public <T> List<T> queryBase(String influxQL, Class<T> clazz) {
        log.debug("执行的最终查询语句为---------->{}", influxQL);
        QueryResult qR = this.queryOri(influxQL);
        List<T> result = Lists.newArrayList();
        qR.getResults().forEach(r -> r.getSeries().forEach(s -> {
            //列名
            List<String> fields = s.getColumns();
            Map<String, String> tags = s.getTags();
            //每一行的值
            s.getValues().forEach(value -> {
                try {
                    T temp = clazz.newInstance();
                    for (int i = 0; i < fields.size(); i++) {
                        ReflectUtil.setFieldValue(temp, fields.get(i), value.get(i));
                    }
                    // modify by hebin， group by的时候，把tag直接塞到result中返回
                    if (!MapUtil.isEmpty(tags)) {
                        tags.forEach((k, v) -> {
                            ReflectUtil.setFieldValue(temp, k, v);
                        });
                    }
                    result.add(temp);
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("{}创建对象错误:{}", clazz, e.getMessage(), e);
                }
            });
        }));
        return result;
    }

    /**
     * 查询指定时间前的最后一条数据
     *
     * @param fields          需要查询的字段，全查给null
     * @param measurement     查询的表
     * @param whereConditions 查询条件
     * @param time            给的的时间
     * @return 对应的数据
     */
    public List<JSONObject> getLast(List<String> fields, String measurement, List<WhereCondition> whereConditions, Date time) {
        if (null == whereConditions) {
            whereConditions = new ArrayList<>();
        }
        whereConditions.add(WhereCondition.builder().field("time").compareKeyWord(SqlKeyword.LE).value(DateFormatUtils.formatUTC(time, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern())).build());
        return this.query(fields, measurement, whereConditions, null, "time desc", 1L, null);
    }

    /**
     * 查询指定时间前的最后一条数据，按照指定tag分组统计
     *
     * @param fields          需要查询的字段，全查给null
     * @param measurement     查询的表
     * @param tag             分组的tag
     * @param whereConditions 查询条件
     * @param time            给的的时间
     * @return 对应的数据
     */
    public List<JSONObject> getLastByTag(List<String> fields, String measurement, String tag, List<WhereCondition> whereConditions, Date time) {
        if (null == whereConditions) {
            whereConditions = new ArrayList<>();
        }
        whereConditions.add(WhereCondition.builder().field("time").compareKeyWord(SqlKeyword.LE).value(DateFormatUtils.formatUTC(time, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern())).build());
        return this.query(fields, measurement, whereConditions, tag, "time desc", 1L, null);
    }

    /**
     * 查询指定时间前的最后一条数据
     *
     * @param fields          需要查询的字段，全查给null
     * @param measurement     查询的表
     * @param whereConditions 查询条件
     * @param time            给的的时间
     * @return 对应的数据
     */
    public List<JSONObject> getFirst(List<String> fields, String measurement, List<WhereCondition> whereConditions, Date time) {
        if (null == whereConditions) {
            whereConditions = new ArrayList<>();
        }
        whereConditions.add(WhereCondition.builder().field("time").compareKeyWord(SqlKeyword.GE).value(DateFormatUtils.formatUTC(time, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern())).build());
        return this.query(fields, measurement, whereConditions, null, "time acs", 1L, null);
    }

    /**
     * 查询指定时间前的最后一条数据
     *
     * @param fields          需要查询的字段，全查给null
     * @param measurement     查询的表
     * @param whereConditions 查询条件
     * @param time            给的的时间
     * @return 对应的数据
     */
    public List<JSONObject> getFirstByTag(List<String> fields, String measurement, String tag, List<WhereCondition> whereConditions, Date time) {
        if (null == whereConditions) {
            whereConditions = new ArrayList<>();
        }
        whereConditions.add(WhereCondition.builder().field("time").compareKeyWord(SqlKeyword.GE).value(DateFormatUtils.formatUTC(time, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern())).build());
        return this.query(fields, measurement, whereConditions, tag, "time acs", 1L, null);
    }

    public List<JSONObject> query(String measurement) {
        return this.query(null, measurement, null, null, null, null, null);
    }


    public List<JSONObject> query(String measurement, List<WhereCondition> whereConditions) {
        return this.query(null, measurement, whereConditions, null, null, null, null);
    }

    public List<JSONObject> query(List<String> fields, String measurement, List<WhereCondition> whereConditions) {
        return this.query(fields, measurement, whereConditions, null, null, null, null);
    }

    public List<JSONObject> query(List<String> fields, String measurement, List<WhereCondition> whereConditions, String order) {
        return this.query(fields, measurement, whereConditions, null, order, null, null);
    }

    public List<JSONObject> query(List<String> fields, String measurement, List<WhereCondition> whereConditions, String order, Long limit) {
        return this.query(fields, measurement, whereConditions, null, order, limit, null);
    }

    public List<JSONObject> query(List<String> fields, String measurement, List<WhereCondition> whereConditions, String groupBy, String order, Long limit, Long offset) {
        String influxQL = getQuery(fields, measurement, whereConditions, groupBy, order, limit, offset);
        return this.queryBase(influxQL);
    }

    public <T> List<T> query(String measurement, Class<T> clazz) {
        return this.query(null, measurement, null, null, null, null, null, clazz);
    }

    public <T> List<T> query(String measurement, List<WhereCondition> whereConditions, Class<T> clazz) {
        return this.query(null, measurement, whereConditions, null, null, null, null, clazz);
    }

    public <T> List<T> query(List<String> fields, String measurement, List<WhereCondition> whereConditions, Class<T> clazz) {
        return this.query(fields, measurement, whereConditions, null, null, null, null, clazz);
    }

    public <T> List<T> query(List<String> fields, String measurement, List<WhereCondition> whereConditions, String order, Class<T> clazz) {
        return this.query(fields, measurement, whereConditions, null, order, null, null, clazz);
    }

    public <T> List<T> query(List<String> fields, String measurement, List<WhereCondition> whereConditions, String order, Long limit, Class<T> clazz) {
        return this.query(fields, measurement, whereConditions, null, order, limit, null, clazz);
    }

    public <T> List<T> query(List<String> fields, String measurement, List<WhereCondition> whereConditions, String groupBy, String order, Long limit, Long offset, Class<T> clazz) {
        String influxQL = getQuery(fields, measurement, whereConditions, groupBy, order, limit, offset);
        return this.queryBase(influxQL, clazz);
    }


    private String getQuery(List<String> fields, String measurement, List<WhereCondition> whereConditions, String groupBy, String order, Long limit, Long offset) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (CollectionUtil.isEmpty(fields)) {
            sb.append("*");
        } else {
            sb.append(String.join(StrUtil.COMMA, fields));
        }
        sb.append(" FROM ").append("\"").append(measurement).append("\"");
        sb.append(whereSql(whereConditions));
        if (StrUtil.isNotBlank(groupBy)) {
            sb.append(" GROUP BY ").append(groupBy);
        }
        if (StrUtil.isNotBlank(order)) {
            sb.append(" ORDER BY ").append(order);
        }
        if (Objects.nonNull(limit)) {
            sb.append(" LIMIT ").append(limit);
        }
        if (Objects.nonNull(offset)) {
            sb.append(" OFFSET ").append(offset);
        }
        return sb.toString();
    }


    private String whereSql(List<WhereCondition> whereConditions) {
        if (CollectionUtil.isEmpty(whereConditions)) {
            return StrUtil.EMPTY;
        }
        List<String> conditions = whereConditions.stream().map(t -> {
            if (t.getValue().startsWith("~")) {
                // 一定不能加空格，空格报错
                return StrUtil.SPACE + t.getField()
                        + StrUtil.SPACE + t.getCompareKeyWord().getSqlSegment()
                        + t.getValue();
            } else {
                return StrUtil.SPACE + t.getField()
                        + StrUtil.SPACE + t.getCompareKeyWord().getSqlSegment()
                        + StrUtil.SPACE + "'" + t.getValue() + "'";
            }
        }).collect(Collectors.toList());
        return " WHERE " + String.join(" AND", conditions);
    }
}
