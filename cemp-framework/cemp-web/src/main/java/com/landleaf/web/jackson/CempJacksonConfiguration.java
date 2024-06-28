package com.landleaf.web.jackson;

import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * Jackson配置
 *
 * @author yue lin
 * @since 2023/5/31 13:40
 */
@Configuration
public class CempJacksonConfiguration {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");


//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return jacksonObjectMapperBuilder ->
//            jacksonObjectMapperBuilder
//                    .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER))
//                    .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER))
//                    .deserializerByType(LocalDate.class, new LocalDateTimeDeserializer(DATE_FORMATTER))
//                    .serializerByType(LocalDate.class, new LocalDateTimeSerializer(DATE_FORMATTER))
//                    .deserializerByType(LocalTime.class, new LocalDateTimeDeserializer(TIME_FORMATTER))
//                    .serializerByType(LocalTime.class, new LocalDateTimeSerializer(TIME_FORMATTER))
//                    .deserializerByType(YearMonth.class, new LocalDateTimeDeserializer(YEAR_MONTH_FORMATTER))
//                    .serializerByType(YearMonth.class, new LocalDateTimeSerializer(YEAR_MONTH_FORMATTER))
//                    .timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//    }

}
