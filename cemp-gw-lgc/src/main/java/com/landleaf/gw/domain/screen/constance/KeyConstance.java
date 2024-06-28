package com.landleaf.gw.domain.screen.constance;

/**
 * redis的key值定义
 */
public interface KeyConstance {
    /**
     * 设备当前状态，存储格式为 device_current_status:{deviceId}
     */
    String DEVICE_CURRENT_STATUS = "device_current_status:";
    /**
     * 前期写死配置
     */
    String PRODUCT_ATTR = "product_attr:";
    String TOKEN_FORMAT = "token:%s";
    String TOKEN_ALL = "token:*";

    /**
     * 字典类型key
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
}
