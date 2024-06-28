package com.landleaf.comm.sta.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KpiUtils {

    public static final String KPI_PREFFIX_SUBITEM = "project.";
    public static final String KPI_PREFFIX_SUBAREA = "area.";

    /**
     * kpi转属性 watermeter.usage.total -> watermeterUsageTotal
     *
     * @param kpi
     * @return
     */
    public static String kpiToProperty(String kpi) {
        String[] kpis = kpi.toLowerCase().split("\\.");
        StringBuilder property = new StringBuilder(kpis[0]);
        for (int i = 1; i < kpis.length; i++) {
            String prop = kpis[i];
            property.append(prop.substring(0, 1).toUpperCase());
            property.append(prop.substring(1));
        }
        return property.toString();
    }

    public static String kpiToProperty(String kpi, Class c) {
        String[] kpis = kpi.split("\\.");
        String kpiProperty = String.join("", kpis);
        //通过kpiProperty 匹配真实的字段
        List<Field> fields = Arrays.asList(c.getDeclaredFields());
        Map<String, String> fieldMap = fields.stream().collect(Collectors.toMap(f -> f.getName().toUpperCase(), f -> f.getName()));
        return fieldMap.get(kpiProperty.toUpperCase());
    }

    public static String getMethod(String prop, String prefix) {
        return String.format("%s%s%s", prefix, prop.substring(0, 1).toUpperCase(), prop.substring(1));
    }

    /**
     * @param kpi
     * @return 1分项 2分区
     */
    public static Integer getKpiTag(String kpi) {
        if (kpi.startsWith(KPI_PREFFIX_SUBITEM)) {
            return 1;
        } else if (kpi.startsWith(KPI_PREFFIX_SUBAREA)) {
            return 2;
        }
        return 0;
    }

    public static Object getValue(Object subitem, Class cla, String methodName) {
        try {
            Method method = cla.getMethod(methodName);
            Object result = method.invoke(subitem);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setValue(Object subitem, Class cla, String methodName, Object value) {
        try {
            Method method = cla.getMethod(methodName, BigDecimal.class);
            method.invoke(subitem, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getYoy(BigDecimal current, BigDecimal last) {
        if (current == null || last == null) {
            return null;
        } else {
            if (current.compareTo(BigDecimal.ZERO) == 0 || last.compareTo(BigDecimal.ZERO) == 0) {
                return null;
            } else {
                return current.subtract(last).multiply(BigDecimal.valueOf(100)).divide(last, 2, RoundingMode.HALF_EVEN).toPlainString();
            }
        }
    }

    public static String getYoy2(BigDecimal current, BigDecimal last) {
        if (current == null || last == null) {
            return null;
        } else {
            if (current.compareTo(BigDecimal.ZERO) == 0 || last.compareTo(BigDecimal.ZERO) == 0) {
                return null;
            } else {
                return current.subtract(last).multiply(BigDecimal.valueOf(100)).divide(last, 1, RoundingMode.DOWN).toPlainString();
            }
        }
    }

    public static String show(BigDecimal val) {
        if (val != null && val.compareTo(BigDecimal.ZERO) != 0) {
            return val.toPlainString();
        } else {
            return null;
        }
    }
}
