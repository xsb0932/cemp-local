package com.landleaf.mqtt.annotation;


import java.lang.annotation.*;
import com.landleaf.mqtt.enums.*;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt消息监听注解类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMessageListener {

    /**
     * topic
     * @return topic
     */
    String topic() default "$SYS/brokers";

    /**
     * 监听qos
     * @return 监听Qos
     */
    MqttQoS mqttQoS() default MqttQoS.AT_LEAST_ONCE;

    /**
     * mqtt消息模式
     * @return mqtt消息模式
     */
    MqttMessageModel mqttMessageModel() default MqttMessageModel.BROADCAST;

    /**
     * 当消息为共享模式时，默认的组id
     * @return 共享模式组id
     */
    int groupIndex() default 1;
}
