package com.landleaf.comm.tenant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户配置
 *
 * @author yue lin
 * @since 2023/6/6 15:36
 */
@Component
@ConfigurationProperties(prefix = "cemp.tenant")
public class TenantProperties {

    /**
     * 需要被忽略的表名
     */
    private List<String> ignoreTables = new ArrayList<>();

    /**
     * 是否全局忽略
     */
    private boolean ignore = false;

    public List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public void setIgnoreTables(List<String> ignoreTables) {
        this.ignoreTables = ignoreTables;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
