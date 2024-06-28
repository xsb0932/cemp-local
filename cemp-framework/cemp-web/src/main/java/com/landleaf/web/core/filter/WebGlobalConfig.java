package com.landleaf.web.core.filter;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;

@SpringBootConfiguration
public class WebGlobalConfig {
    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        ArrayList<String> list = CollectionUtil.newArrayList("*");
        config.setAllowedOriginPatterns(list);
//        config.setAllowedOrigins(list);
        config.setAllowedHeaders(list);
        config.setAllowedMethods(list);
        // 创建 UrlBasedCorsConfigurationSource 对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
