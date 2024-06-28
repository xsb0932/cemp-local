package com.landleaf.energy.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class JobUtil {
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static int TYPE_HOUR = 0;
    public static int TYPE_DAY = 1;
    public static int TYPE_MONTH = 2;
    public static int TYPE_YEAR = 3;

    /**
     * 将开始和结束时间拆分为对应的时间的list。
     *
     * @param startTime
     * @param endTime
     * @param type
     * @return
     */
    public static List<LocalDateTime> dealTimeStr2Array(String startTime, String endTime, int type) {
        LocalDateTime start;
        LocalDateTime end;
        List<LocalDateTime> result = new ArrayList<>();
        if (TYPE_HOUR == type) {
            // 分时
            start = LocalDateTime.parse(startTime, dateTimeFormatter);
            result.add(start);
            end = LocalDateTime.parse(endTime, dateTimeFormatter);
            while (start.isBefore(end)) {
                start = start.plusHours(1);
                result.add(start);
            }
        } else if (TYPE_DAY == type) {
            // 分日
            startTime += " 00:00:00";
            endTime += " 00:00:00";
            start = LocalDateTime.parse(startTime, dateTimeFormatter);
            result.add(start);
            end = LocalDateTime.parse(endTime, dateTimeFormatter);
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                result.add(start);
            }
        } else if (TYPE_MONTH == type) {
            // 分月。分月逻辑比较奇怪，按着文档， 直接在将日期设定为从上月23号到本月6号
            startTime += "-01 00:00:00";
            endTime += "-01 00:00:00";
            start = LocalDateTime.parse(startTime, dateTimeFormatter);
            LocalDateTime temp = start.minusMonths(1);
            temp = temp.withDayOfMonth(24);
            result.add(temp);
            for (int i = 0; i < 5; i++) {
                temp = temp.plusDays(1);
                result.add(temp);
            }
            result.add(start);
            temp = start.plusDays(1);
            result.add(temp);
            for (int i = 0; i < 5; i++) {
                temp = temp.plusDays(1);
                result.add(temp);
            }
            end = LocalDateTime.parse(endTime, dateTimeFormatter);
            while (start.isBefore(end)) {
                start = start.plusMonths(1);
                temp = start.minusMonths(1);
                temp = temp.withDayOfMonth(24);
                result.add(temp);
                for (int i = 0; i < 5; i++) {
                    temp = temp.plusDays(1);
                    result.add(temp);
                }
                result.add(start);
                temp = start.plusDays(1);
                result.add(temp);
                for (int i = 0; i < 5; i++) {
                    temp = temp.plusDays(1);
                    result.add(temp);
                }
            }
        } else if (TYPE_YEAR == type) {
            // 分年，同样，将逻辑置为前置到去年12月23日，到今年的1月6号
            startTime += "-01-01 00:00:00";
            endTime += "-01-01 00:00:00";
            start = LocalDateTime.parse(startTime, dateTimeFormatter);
            LocalDateTime temp = start.minusYears(1);
            temp = temp.plusMonths(11);
            temp = temp.withDayOfMonth(24);
            result.add(temp);
            for (int i = 0; i < 5; i++) {
                temp = temp.plusDays(1);
                result.add(temp);
            }
            result.add(start);
            temp = start.plusDays(1);
            result.add(temp);
            for (int i = 0; i < 5; i++) {
                temp = temp.plusDays(1);
                result.add(temp);
            }
            end = LocalDateTime.parse(endTime, dateTimeFormatter);
            while (start.isBefore(end)) {
                start = start.plusYears(1);
                temp = start.minusYears(1);
                temp = temp.withMonth(12);
                temp = temp.withDayOfMonth(24);
                result.add(temp);
                for (int i = 0; i < 5; i++) {
                    temp = temp.plusDays(1);
                    result.add(temp);
                }
                result.add(start);
                temp = start.plusDays(1);
                result.add(temp);
                for (int i = 0; i < 5; i++) {
                    temp = temp.plusDays(1);
                    result.add(temp);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<LocalDateTime> lis = dealTimeStr2Array("2024", "2027", TYPE_YEAR);
        lis.forEach(i -> {
            System.out.println(i.toString());
        });
    }
}
