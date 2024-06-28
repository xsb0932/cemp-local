package com.landleaf.gateway.security.config;

import com.landleaf.gateway.security.context.ServerSecurityContextRepositoryImpl;
import com.landleaf.gateway.security.core.handler.AccessDeniedHandlerImpl;
import com.landleaf.gateway.security.core.handler.AuthenticationEntryPointImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

/**
 * Spring Security 配置类，主要用于相关组件的配置
 *
 * @author 张力方
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableWebFluxSecurity
public class CempSecurityConfiguration {

    @Bean
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

    /**
     * 认证失败处理类 Bean
     */
    @Bean
    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository() {
        return new ServerSecurityContextRepositoryImpl(securityProperties());
    }

    /**
     * 权限不够处理器 Bean
     */
    @Bean
    public ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {


        http
                // 开启跨域
//                .cors(Customizer.withDefaults())
                // CSRF 禁用，因为不使用 Session
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(serverSecurityContextRepository())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(serverAuthenticationEntryPoint())
                                .accessDeniedHandler(serverAccessDeniedHandler()))
                // 设置每个请求的权限
                // 全局共享规则
                .authorizeExchange(authorizeHttpRequests -> authorizeHttpRequests
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(securityProperties().getPermitAllUrls().toArray(String[]::new)).permitAll()
                        // 兜底规则，必须认证
                        .anyExchange().authenticated())
        ;

        return http.build();
    }

//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedOriginPattern("*"); // 允许跨域访问的域名，* 表示允许所有域名
//        corsConfiguration.addAllowedHeader("*"); // 允许跨域访问的请求头
//        corsConfiguration.addAllowedMethod("*"); // 允许跨域访问的请求方法
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsWebFilter(source);
//    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource (new PathPatternParser());
//        CorsConfiguration corsConfig = new CorsConfiguration ();
//        // 允许所有请求方法
//        corsConfig.addAllowedMethod ("*");
//        // 允许所有域，当请求头
//        corsConfig.addAllowedOrigin ("*");
//        // 允许全部请求头
//        corsConfig.addAllowedHeader ("*");
//        // 允许携带 Authorization 头
//        corsConfig.setAllowCredentials (true);
//        // 允许全部请求路径
//        source.registerCorsConfiguration ("/**", corsConfig);
//        return source;
//    }

}
