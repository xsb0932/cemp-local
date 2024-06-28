package com.landleaf.pgsql.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizSequenceEnum {
    /**
     * 全局唯一ID序列枚举
     */
    AREA("biz_id_area_seq", "空间分区", "A", "%06d"),
    CATEGORY("biz_id_category_seq", "品类", "PC", "%04d"),
    DEVICE("biz_id_device_seq", "设备", "D", "%012d"),
    NODE("biz_id_node_seq", "管理节点", "N", "%08d"),
    PRODUCT("biz_id_product_seq", "产品","PK", "%08d"),
    PROJECT("biz_id_project_seq", "项目","PJ", "%08d"),
    GATEWAY("biz_id_gateway_seq", "网关","GW", "%08d"),
    TENANT("biz_id_tenant_seq", "租户","T", "%04d"),
    // 数据库需要新增 CREATE SEQUENCE biz_id_alarm_seq START 1;
    ALARM("biz_id_alarm_seq", "告警","A", "%09d"),
    // 数据库需要新增 CREATE SEQUENCE biz_id_event_seq START 1;
    EVENT("biz_id_event_seq", "事件","E", "%09d"),
    // 数据库需要新增 CREATE SEQUENCE biz_id_msg_seq START 1;
    MSG("biz_id_msg_seq", "消息","C", "%08d"),
    // 数据库需要新增 CREATE SEQUENCE biz_id_rule_seq START 1;
    RULE("biz_id_rule_seq", "规则","C", "%04d"),
    ;

    /**
     * sequence
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;
    /**
     * 前缀
     */
    private final String prefix;
    /**
     * 长度
     */
    private final String reg;
}
