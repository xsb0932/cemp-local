package com.landleaf.web.core.converter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * java8时间转换
 *
 * @author Tycoon
 * @since 2023/8/18 14:33
 **/
@Configuration
public class JavaTimeConverter {

    @Component
    public static class StringLocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    @Component
    public static class StringYearMonthConverter implements Converter<String, YearMonth> {
        @Override
        public YearMonth convert(String source) {
            return YearMonth.parse(source, DateTimeFormatter.ofPattern("yyyy-MM"));
        }
    }

}
