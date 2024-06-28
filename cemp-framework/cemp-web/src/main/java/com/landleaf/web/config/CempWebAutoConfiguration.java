package com.landleaf.web.config;

import cn.hutool.core.collection.CollectionUtil;
import com.landleaf.comm.constance.WebFilterOrderEnum;
import com.landleaf.comm.license.LicenseCheck;
import com.landleaf.comm.tenant.TenantProperties;
import com.landleaf.web.core.filter.XssFilter;
import com.landleaf.web.interceptor.LicenseHandlerInterceptor;
import com.landleaf.web.interceptor.TenantHandlerInterceptor;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

/**
 * @author eason
 */
@Configuration
@RequiredArgsConstructor
public class CempWebAutoConfiguration implements WebMvcConfigurer {

    private final TenantProperties tenantProperties;

    private final LicenseCheck licenseCheck;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantHandlerInterceptor(tenantProperties))
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger**/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v3/**")
                .excludePathPatterns("/doc.html");
        registry.addInterceptor(new LicenseHandlerInterceptor(licenseCheck))
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger**/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v3/**")
                .excludePathPatterns("/doc.html");
    }

    @Value("${cemp.file-path:}")
    private String filePath;

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        ArrayList<String> list = CollectionUtil.newArrayList("*");
        config.setAllowedOriginPatterns(list);
        config.setAllowedHeaders(list);
        config.setAllowedMethods(list);
        // 创建 UrlBasedCorsConfigurationSource 对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
    }

    /**
     * 创建 XssFilter Bean，解决 Xss 安全问题
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilter() {
        return createFilterBean(new XssFilter(), WebFilterOrderEnum.XSS_FILTER);
    }

    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 和页面有关的图片放在项目的img目录下
        registry.addResourceHandler("/api/file/**").
                addResourceLocations("File:" + filePath);
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }



}
