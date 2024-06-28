package com.landleaf.comm.sta.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.landleaf.comm.sta.enums.StaTimePeriodEnum;
import org.springframework.cglib.core.Local;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
    public final static SimpleDateFormat SD_DT_FMT_HOUR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat SD_DT_FMT_DAY = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat SD_DT_FMT_MONTH = new SimpleDateFormat("yyyy-MM");
    public final static SimpleDateFormat SD_DT_FMT_YEAR = new SimpleDateFormat("yyyy");
    public final static DateTimeFormatter LC_DT_FMT_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter LC_DT_FMT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter LC_DT_FMT_TIME_4_EXPORT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public final static String STR_DT_FMT_DAY ="yyyy-MM-dd";

    public static void main(String[] args) {
//        LocalDate begin = LocalDate.now();
////        LocalDate end = begin.plusDays(100);
        LocalDate begin = LocalDate.of(2024,2,1);
        LocalDate end = begin.plusMonths(1);
        List<LocalDate> list =  DateUtils.getDaysBetween(begin,end);
        System.out.println(list.size());
    }

    public static String get(String day){
        DateTime dateTime = DateUtil.offsetDay(DateUtil.parseDate(day),-1);
        return DateUtil.format(dateTime,"yyyy-MM-dd");
    }

    public static String parseDate(Date date){
        return DateUtil.format(date,"yyyy-MM-dd");
    }

    public static String get(String day,String staTimePeriod ){
        if(staTimePeriod.equals(StaTimePeriodEnum.DAY.getType())){
            DateTime dateTime = DateUtil.offsetDay(DateUtil.parseDate(day),1);
            return DateUtil.format(dateTime,"yyyy-MM-dd HH:mm:ss");
        }
        if(staTimePeriod.equals(StaTimePeriodEnum.MONTH.getType())){
            DateTime dateTime = DateUtil.offsetMonth(DateUtil.parseDate(day),1);
            return DateUtil.format(dateTime,"yyyy-MM-dd HH:mm:ss");
        }
        if(staTimePeriod.equals(StaTimePeriodEnum.YEAR.getType())){
            DateTime dateTime = DateUtil.offsetMonth(DateUtil.parseDate(day),12);
            return DateUtil.format(dateTime,"yyyy-MM-dd HH:mm:ss");
        }
        return null;
    }

    public static String getToday(LocalDateTime timeNow){
        return timeNow.format(LC_DT_FMT_DAY);
    }

    public static String getCurrent(LocalDateTime timeNow){
        return timeNow.format(LC_DT_FMT_TIME_4_EXPORT);
    }

    public static String date2Str(LocalDateTime time, DateTimeFormatter formatter){
        return time.format(formatter);
    }

    public static String getTomorrow(LocalDateTime timeNow){
        return (timeNow.plus(1, ChronoUnit.DAYS)).format(LC_DT_FMT_DAY);
    }

    public static String fmt2Str(Date date, SimpleDateFormat fmt){
        return fmt.format(date);
    }

    public static String fmt2Str(String date, SimpleDateFormat fmt){
        try {
            return fmt.format(fmt.parse(date));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getMonthLastDay(String date, String pattern){
        DateTime dateTime = DateUtil.endOfMonth(DateUtil.parseDate(date));
        return DateUtil.format(dateTime,pattern);
    }

    public static String getDate(DateTime date, Integer monthOffset , Integer dayOffset ,String fmt){
        if(monthOffset != null){
            date = DateUtil.offsetMonth(date,monthOffset);
        }
        if(dayOffset != null){
            date = DateUtil.offsetDay(date,dayOffset);
        }
        return DateUtil.format(date,fmt);
    }

    public static List<LocalDate> getTotalDaysByMonth(String year,String month){
        List<LocalDate> days = new ArrayList<>();           //当月所有天
        YearMonth ym =  YearMonth.of(Integer.valueOf(year),Integer.valueOf(month));
        LocalDate beginOfMonth = ym.atDay(1);
        LocalDate endOfMonth = ym.atEndOfMonth();
        Long d = 0L;
        while(beginOfMonth.plusDays(d).compareTo(endOfMonth) <= 0){
            days.add(beginOfMonth.plusDays(d));
            d ++;
        }
        return days;
    }

    public static List<LocalDate> getDaysBetween(LocalDate begin , LocalDate end){
        List<LocalDate> list = new ArrayList<>();
        if(begin.compareTo(end) >= 0){
            return null;
        }else{
            while(begin.compareTo(end) < 0){
                list.add(begin);
                begin = begin.plusDays(1L);
            }
        }
        return list;
    }

    public static List<YearMonth> getMonthsBetween(YearMonth begin , YearMonth end){
        List<YearMonth> list = new ArrayList<>();
        if(begin.compareTo(end) >= 0){
            return null;
        }else{
            while(begin.compareTo(end) < 0){
                list.add(begin);
                begin = begin.plusMonths(1L);
            }
        }
        return list;
    }

}
