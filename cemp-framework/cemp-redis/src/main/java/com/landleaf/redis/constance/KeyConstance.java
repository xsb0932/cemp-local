package com.landleaf.redis.constance;

/**
 * redis的key值定义
 */
public interface KeyConstance {
    /**
     * 设备当前状态，存储格式为 device_current_status:{deviceId}
     */
    String DEVICE_CURRENT_STATUS = "device_current_status:";

    /**
     * 设备当前状态，存储格式为 device_current_status_v1:{deviceId}
     */
    String DEVICE_CURRENT_STATUS_V1 = "device_current_status_v1:";

    /**
     * 设备开关机状态
     */
    String DEVICE_POWER_ON_STATUS = "device_power_on_status:";
    /**
     * 前期写死配置
     */
    String PRODUCT_ATTR = "product_attr:";
    String TOKEN_FORMAT = "token:%s";
    String TOKEN_ALL = "token:*";

    /**
     * 字典类型key b
     * 系统字典为当前
     * 租户字典在后面拼接_tenantId（dict_type_1）
     */
    String DICT_TYPE = "dict_type";

    /**
     * 系统字典数据key
     * 字典编码
     */
    String SYSTEM_DICT_DATA = "system_dict_data";

    /**
     * 租户字典数据key
     * 租户id-字典编码
     */
    String TENANT_DICT_DATA = "tenant_dict_data:%s";

    /**
     * 消息执行结果返回
     */
    String CMD_EXEC_RESULT = "exec_result:";

    /**
     * 天气配置缓存
     */
    String WEATHER_CACHE = "weather_cache";

    /**
     * 忘记密码口令
     */
    String FORGOT_PASSWORD = "FORGOT_PASSWORD_";

    /**
     * 关系转换的key，将outerId转为bizDeviceId
     */
    String OUTER_DEVICE_RELATION = "outer_device_relation";

    /**
     * 设备信息缓存key
     */
    String DEVICE_INFO_CACHE = "device_info_cache";

    /**
     * 设备信息缓存key
     */
    String PROD_INFO_CACHE = "prod_info_cache";

    /**
     * 设备参数缓存key
     */
    String DEVICE_PARAMETER_VALUE_CACHE = "device_parameter_value_cache";

    /**
     * 产品参数缓存key
     */
    String PRODUCT_PARAMETER_VALUE_CACHE = "product_parameter_value_cache";

    /**
     * 格式为gateId_pkId_sourceDeviceId;
     */
    String OUTER_DEVICE_KEY = "%s_%s_%s";

    /**
     * 关系转换的key，将bizDeviceId转为outerId
     */
    String DEVICE_OUTER_RELATION = "device_outer_relation";

    /**
     * 格式为gateId_pkId_biz_device_id;
     */
    String DEVICE_OUTER_KEY = "%s_%s_%s";

    /**
     * 设备的timeout
     */
    String DEVICE_TIME_OUT_CACHE = "device_time_out_cache";

    /**
     * 设备的gateway信息
     */
    String DEVICE_GATEWAY_CACHE = "device_gateway_cache";

    /**
     * 软网关启动lock
     */
    String GW_LOCK_KEY = "gw_lock_";

    /**
     * 动态的notice的key
     */
    String DYNAMICS_NOTICE_KEY = "dynamics_notice_key";

    /**
     * 动态的notice的key, dynamics_notice_detail_bizDevice_param
     */
    String NOTICE_KEY_PREFIX = "dynamics_notice_detail:%s_%s";

    /**
     * 动态的notice的要过期的key
     */
    String NOTICE_EXPIRE_KEY = "dynamics_notice_expire_key:";

    /**
     * 未确认的告警数量
     */
    String UNCONFIRMED_ALARM_COUNT_CACHE = "unconfirmed_count_cache:%s";

    /**
     * messaging组件中gateway的缓存
     */
    String MESSAGING_GATEWAY_CACHE = "messaging_gateway_cache";

    /**
     * messaging组件中prod的alarm配置信息
     */
    String MESSAGING_PROD_ALARM_CONFIG_CACHE = "messaging_prod_alarm_config_cache";

    /**
     * messaging组件中prod的event配置信息
     */
    String MESSAGING_PROD_EVENT_CONFIG_CACHE = "messaging_prod_event_config_cache";

    /**
     * messaging组件中prod的service配置信息
     */
    String MESSAGING_PROD_SERVICE_CONFIG_CACHE = "messaging_prod_service_config_cache";

    /**
     * 规则缓存编号
     */
    String RULE = "rule";

    /**
     * 缓存触发编号
     */
    String RULE_TRIGGER = "rule_trigger";

    /**
     * 告警推送规则缓存
     * alarm_push_rule:{tenantId}
     */
    String ALARM_PUSH_RULE = "alarm_push_rule";
}
