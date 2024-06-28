package com.landleaf.influx.conditions;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.influxdb.annotations.Measurement;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.influx.model.TimeColumn;
import com.landleaf.influx.util.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简单的查询条件封装
 *
 * @author eason
 */
public class QueryBuilder<T extends TimeColumn> {
    private Class<T> entityClass;
    private String rangeFlux;
    private final List<String> filterFluxList = new ArrayList<>();

    public QueryBuilder() {
    }

    public QueryBuilder(Class<T> entityClass) {
        Measurement measurement = entityClass.getAnnotation(Measurement.class);
        if (null == measurement) {
            throw new BusinessException("need influx measurement annotation");
        }
        this.filterFluxList.add(" |> filter(fn: (r) => r[\"_measurement\"] == \"" + measurement.name() + "\")\n");
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    public static <T extends TimeColumn> QueryBuilder<T> of(Class<T> entityClass) {
        return new QueryBuilder<>(entityClass);
    }

    public QueryBuilder<T> last(LocalDateTime stop) {
        if (null == stop) {
            throw new BusinessException("need influx stop time");
        }
        this.rangeFlux = " |> range(start:0, stop:" + stop.atZone(ZoneId.systemDefault()).toInstant().toString() + ")\n";
        return this;
    }

    public QueryBuilder<T> range(LocalDateTime start, LocalDateTime stop) {
        if (null == start) {
            throw new BusinessException("need influx start time");
        }
        this.rangeFlux = " |> range(start:" + start.atZone(ZoneId.systemDefault()).toInstant().toString();
        if (null != stop) {
            this.rangeFlux = this.rangeFlux + ", stop:" + stop.atZone(ZoneId.systemDefault()).toInstant().toString();
        }
        this.rangeFlux = this.rangeFlux + ")\n";
        return this;
    }

    public QueryBuilder<T> eq(SFunction<T, ?> column, String val) {
        if (null != val) {
            filterFluxList.add(" |> filter(fn: (r) => r[\"" + getColumnName(column) + "\"] == \"" + val + "\")\n");
        }
        return this;
    }

    public QueryBuilder<T> in(SFunction<T, ?> column, List<String> valList) {
        if (CollectionUtil.isNotEmpty(valList)) {
            String columnName = getColumnName(column);
            filterFluxList.add(" |> filter(fn: (r) => " +
                    valList.stream()
                            .map(attr -> "r[\"" + columnName + "\"] == \"" + attr + "\"")
                            .collect(Collectors.joining(" or ")) + ")\n");
        }
        return this;
    }

    private String getColumnName(SFunction<T, ?> column) {
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = column.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(column);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);

        // 从lambda信息取出method、field、class等
        String implMethodName = serializedLambda.getImplMethodName();
        // 确保方法是符合规范的get方法，boolean类型是is开头
        if (!implMethodName.startsWith("is") && !implMethodName.startsWith("get")) {
            throw new RuntimeException("column get method: " + implMethodName + " invalid");
        }

        // get方法开头为 is 或者 get，将方法名 去除is或者get，然后首字母小写，就是属性名
        int prefixLen = implMethodName.startsWith("is") ? 2 : 3;

        String fieldName = implMethodName.substring(prefixLen);
        String firstChar = fieldName.substring(0, 1);
        return fieldName.replaceFirst(firstChar, firstChar.toLowerCase());
    }

    public String getRangeFlux() {
        return this.rangeFlux;
    }

    public String getFilterFlux() {
        if (CollectionUtil.isEmpty(this.filterFluxList)) {
            return StrUtil.EMPTY;
        }
        return String.join("", this.filterFluxList);
    }

}
