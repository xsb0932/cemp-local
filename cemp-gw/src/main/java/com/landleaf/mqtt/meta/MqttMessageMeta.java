package com.landleaf.mqtt.meta;

import com.landleaf.mqtt.enums.MqttMessageModel;
import com.landleaf.mqtt.enums.MqttQoS;
import lombok.Data;

/**
 * 替代MqttMessageListener，存放元数据
 *
 * @author Yang
 */
@Data
public class MqttMessageMeta {
    /**
     * topic
     */
    String topic = "$SYS/brokers";

    /**
     * 监听qos
     */
    MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;

    /**
     * mqtt消息模式
     */
    MqttMessageModel mqttMessageModel = MqttMessageModel.BROADCAST;

    /**
     * 当消息为共享模式时，默认的组id
     */
    int groupIndex = 1;
    
}
