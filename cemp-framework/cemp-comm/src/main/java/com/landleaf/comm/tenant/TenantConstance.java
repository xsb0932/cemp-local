package com.landleaf.comm.tenant;

/**
 * 租户常量字段
 *
 * @author yue lin
 * @since 2023/5/31 16:28
 */
public class TenantConstance {

    /**
     * 多租户ID字段名
     */
    public static final String DATASOURCE_TENANT_FILED = "tenant_id";

    /**
     * 多租户ID请求头Key
     */
    public static final String HEADER_TENANT_ID = "tenant_id";

    /**
     * 多租户是否忽略请求头Key
     */
    public static final String HEADER_TENANT_IGNORE= "tenant_ignore";

}
