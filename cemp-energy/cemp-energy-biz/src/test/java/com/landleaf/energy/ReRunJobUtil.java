package com.landleaf.energy;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReRunJobUtil {
    /**
     * 重跑线上统计任务
     *
     * @param args
     */
    public static void main(String[] args) {
        // 开始时间
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 22, 1, 1, 0);
        // 当前时间
        LocalDateTime now = LocalDateTime.of(2023, 11, 23, 1, 1, 0);
        // url
        String deviceHour = "http://emp.landib.com:611/cemp-energy/job/device/sta-hour/manual";
        String deviceDay = "http://emp.landib.com:611/cemp-energy/job/device/sta-day/manual";
        String deviceMonth = "http://emp.landib.com:611/cemp-energy/job/device/sta-month/manual";
        String deviceYear = "http://emp.landib.com:611/cemp-energy/job/device/sta-year/manual";

        String areaHour = "http://emp.landib.com:611/cemp-energy/sta-subarea/statistics/hour";
        String areaDay = "http://emp.landib.com:611/cemp-energy/sta-subarea/statistics/day";
        String areaMonth = "http://emp.landib.com:611/cemp-energy/sta-subarea/statistics/month";
        String areaYear = "http://emp.landib.com:611/cemp-energy/sta-subarea/statistics/year";

        String itemHour = "http://emp.landib.com:611/cemp-energy/sta-subitem/statistics/hour";
        String itemDay = "http://emp.landib.com:611/cemp-energy/sta-subitem/statistics/day";
        String itemMonth = "http://emp.landib.com:611/cemp-energy/sta-subitem/statistics/month";
        String itemYear = "http://emp.landib.com:611/cemp-energy/sta-subitem/statistics/year";
        // token
        String token = "8783b9a2452e459784794faf86656d8d";
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put("Authorization", token);
        List<HttpResponse> responseList = new ArrayList<>();
        // 重跑不超过当前
        while (startDate.compareTo(now) < 0) {
            String time = LocalDateTimeUtil.formatNormal(startDate);
            System.out.println("-时 " + time);
            // 小时
            responseList.add(
                    HttpUtil.createGet(deviceHour)
                            .headerMap(headerMap, true)
                            .form("time", LocalDateTimeUtil.format(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd")))
                            .execute()
            );
            responseList.add(
                    HttpUtil.createGet(areaHour)
                            .headerMap(headerMap, true)
                            .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH")))
                            .execute()
            );
            responseList.add(
                    HttpUtil.createGet(itemHour)
                            .headerMap(headerMap, true)
                            .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH")))
                            .execute()
            );
            if (startDate.getHour() == 0) {
                System.out.println("--日 " + time);
                responseList.add(
                        HttpUtil.createGet(deviceDay)
                                .headerMap(headerMap, true)
                                .form("time", LocalDateTimeUtil.format(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd")))
                                .execute()
                );
                responseList.add(
                        HttpUtil.createGet(areaDay)
                                .headerMap(headerMap, true)
                                .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .execute()
                );
                responseList.add(
                        HttpUtil.createGet(itemDay)
                                .headerMap(headerMap, true)
                                .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .execute()
                );
            }
            if (startDate.getDayOfMonth() == 1 && startDate.getHour() == 0) {
                System.out.println("---月 " + time);
//                responseList.add(
//                        HttpUtil.createGet(deviceMonth)
//                                .headerMap(headerMap, true)
//                                .form("time", LocalDateTimeUtil.format(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd")))
//                                .execute()
//                );
//                responseList.add(
//                        HttpUtil.createGet(areaMonth)
//                                .headerMap(headerMap, true)
//                                .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy-MM")))
//                                .execute()
//                );
//                responseList.add(
//                        HttpUtil.createGet(itemMonth)
//                                .headerMap(headerMap, true)
//                                .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy-MM")))
//                                .execute()
//                );
            }
            if (startDate.getMonthValue() == 1 && startDate.getDayOfMonth() == 1 && startDate.getHour() == 0) {
                System.out.println("----年 " + time);
//                responseList.add(
//                        HttpUtil.createGet(deviceYear)
//                                .headerMap(headerMap, true)
//                                .form("time", LocalDateTimeUtil.format(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd")))
//                                .execute()
//                );
//                responseList.add(
//                        HttpUtil.createGet(areaYear)
//                                .headerMap(headerMap, true)
//                                .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy")))
//                                .execute()
//                );
//                responseList.add(
//                        HttpUtil.createGet(itemYear)
//                                .headerMap(headerMap, true)
//                                .form("times", LocalDateTimeUtil.format(startDate.minusHours(1), DateTimeFormatter.ofPattern("yyyy")))
//                                .execute()
//                );
            }
            startDate = startDate.plusHours(1L);
        }
        for (HttpResponse httpResponse : responseList) {
            System.out.println(httpResponse.isOk() + " " + httpResponse.body());
        }
    }
}
