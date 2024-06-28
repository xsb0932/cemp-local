package com.landleaf.pgsql.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.google.common.collect.Sets;
import com.landleaf.comm.tenant.TenantConstance;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.tenant.TenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.Optional;
import java.util.Set;

/**
 * 多租户拦截器配置
 *
 * @author yue lin
 * @since 2023/5/30 17:09
 */
public class MyTenantLineHandler implements TenantLineHandler {

    /**
     * 忽略租户的表名
     */
    private final static Set<String> IGNORE_TABLE = Sets.newHashSet("tb_address",
            "tb_module", "tb_tenant");

    @Override
    public Expression getTenantId() {
        return Optional.ofNullable(TenantContext.getTenantId())
                .map(LongValue::new)
                .orElseThrow(() -> new NullPointerException("当前请求不存在多租户编号"));
    }

    @Override
    public String getTenantIdColumn() {
        return TenantConstance.DATASOURCE_TENANT_FILED;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // todo 添加忽略表信息
        // todo 改为配置项
        return TenantContext.isIgnore() ||
                SpringUtil.getBean(TenantProperties.class).isIgnore() ||
                SpringUtil.getBean(TenantProperties.class).getIgnoreTables().contains(tableName);
    }

}
