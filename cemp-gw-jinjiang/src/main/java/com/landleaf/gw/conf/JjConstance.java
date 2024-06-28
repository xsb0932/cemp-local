package com.landleaf.gw.conf;

public interface JjConstance {
    /**
     * 内部租户编号
     */
    String BIZ_TENANT_ID = "T0001";

    /**
     * 内部项目编号
     */
    String BIZ_PROJECT_ID="PJ00000001";

    /**
     * 内部管理节点编号
     */
    String BIZ_NODE_ID="N00000002";

    /**
     * 空调的编号
     */
    String AIR_CONDITION_PROD_ID = "PK00000004";

    /**
     * 表计的采集供应商
     */
    String GAUGES_SUPPLIER_ID="S00001";

    /**
     * 空调的采集供应商
     */
    String AIR_CONDITION_SUPPLIER_ID="S00002";

    /**
     * 设备在线
     */
    int CST_ONLINE = 1;

    /**
     * 设备离线
     */
    int CST_OFFLINE = 0;
}
