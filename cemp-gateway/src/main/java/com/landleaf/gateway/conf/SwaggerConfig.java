//package com.landleaf.gateway.conf;
//
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
//import org.springdoc.core.properties.SwaggerUiConfigProperties;
//import org.springframework.cloud.gateway.route.RouteDefinition;
//import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Component
//public class SwaggerConfig {
//
//    public static final String API_URI = "/%s/v3/api-docs";
//
//    @Bean
//    public List<GroupedOpenApi> apis(SwaggerUiConfigProperties saggerUiConfigProperties, RouteDefinitionLocator routeDefinitionLocator) {
//        List<GroupedOpenApi> groups = new ArrayList<>();
//
//        // 获取所有可用的服务地址
//        List<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions().collectList().block();
//        if (CollectionUtils.isEmpty(definitions)) {
//            return groups;
//        }
//        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();
//        definitions.stream().filter(route -> route.getUri().getHost() != null)
//                .distinct()
//                .forEach(route -> {
//                            String name = route.getUri().getHost();
//                            if (name.startsWith("cemp-")) {
//                                AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
//                                swaggerUrl.setName(name);
//                                swaggerUrl.setUrl(String.format(API_URI, name.replace("cemp-", "")));
//                                urls.add(swaggerUrl);
//                            }
//                        }
//                );
//
//        saggerUiConfigProperties.setUrls(urls);
//        return groups;
//    }
//
//}