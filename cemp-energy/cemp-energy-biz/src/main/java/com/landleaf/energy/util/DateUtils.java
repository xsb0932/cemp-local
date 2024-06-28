package com.landleaf.energy.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.landleaf.energy.domain.enums.StaTimePeriodEnum;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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
        System.out.println(DateUtils.getCurrent(LocalDateTime.now()));
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

    public static String getTomorrow(LocalDateTime timeNow){
        return (timeNow.plus(1, ChronoUnit.DAYS)).format(LC_DT_FMT_DAY);
    }

    public static String fmt2Str(Date date, SimpleDateFormat fmt){
        return fmt.format(date);
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

}
