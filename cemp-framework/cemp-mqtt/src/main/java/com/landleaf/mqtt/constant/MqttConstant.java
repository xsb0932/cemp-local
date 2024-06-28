package com.landleaf.mqtt.constant;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description Mqtt相关常量
 */
public interface MqttConstant {

    /**
     * 共享topic前缀
     */
    String SHARE_TOPIC_PREFIX = "$share/g{index}/";

    /**
     * 替换字符串
     */
    String REPLACE_INDEX = "{index}";

    /**
     *  mqtt客户端 接收标识
     */
    String FIX_RECEIVE = "receive";

    /**
     * mqtt客户端 发送标识
     */
    String FIX_SEND = "send";

    /**
     * 重试时间
     */
    int RETRY_TIME = 3;
}
