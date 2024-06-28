package com.landleaf.pgsql.conf;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.landleaf.pgsql.extension.CempSqlInjector;
import com.landleaf.pgsql.handler.DefaultDataObjectFieldHandler;
import com.landleaf.pgsql.handler.MyTenantLineHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author eason
 */
@Configuration
public class CempMybatisPlusConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(new MyTenantLineHandler());

        mybatisPlusInterceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return mybatisPlusInterceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {
        return new DefaultDataObjectFieldHandler();
    }

    @Bean
    public DefaultSqlInjector cempSqlInjector() {
        return new CempSqlInjector();
    }

}
