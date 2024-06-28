package com.landleaf.mqtt.support;

import com.landleaf.mqtt.constant.MqttConstant;
import com.landleaf.mqtt.enums.MqttMessageModel;
import com.landleaf.mqtt.enums.MqttQoS;
import com.landleaf.mqtt.meta.MqttMessageMeta;
import lombok.Data;

/**
 * 网关特殊处理，需要实例化多个bean，不通过MqttMessageListener
 *
 * @author Yang
 */
@Data
public class CustomMqttListenerContainer extends DefaultMqttListenerContainer {
    private MqttMessageMeta mqttMessageMeta;

    /**
     * 监听topic
     */
    @Override
    public void subscribe() {
        String topic = mqttMessageMeta.getTopic();
        MqttQoS mqttQoS = mqttMessageMeta.getMqttQoS();
        MqttMessageModel mqttMessageModel = mqttMessageMeta.getMqttMessageModel();
        if (MqttMessageModel.SHARE.equals(mqttMessageModel)) {
            int groupIndex = mqttMessageMeta.getGroupIndex();
            topic = MqttConstant.SHARE_TOPIC_PREFIX.replace(MqttConstant.REPLACE_INDEX, String.valueOf(groupIndex)) + topic;
        }
        super.getReceiveClient().subscribe(topic, mqttQoS);
    }
}
