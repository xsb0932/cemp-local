package com.landleaf.mqtt.enums;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt qos
 */
public enum MqttQoS {

    /**
     * 至多一次
     */
    AT_MOST_ONCE(0),
    /**
     * 至少一次
     */
    AT_LEAST_ONCE(1),

    /**
     * 仅一次
     */
    EXACTLY_ONCE(2),

    /**
     * 失败
     */
    FAILURE(-1);

    private final int value;

    MqttQoS(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MqttQoS valueOf(int value) {
         switch (value) {
             case 0 :
                 return AT_MOST_ONCE;
             case 1 :
                 return AT_LEAST_ONCE;
             case 2 :
                 return EXACTLY_ONCE;
             case -1 :
                 return FAILURE;
             default :
                 throw new IllegalArgumentException("invalid QoS: " + value);
        }
    }
}
