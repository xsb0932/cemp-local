package com.landleaf.gw.listener;


import com.landleaf.gw.service.JsConvertService;
import com.landleaf.mqtt.core.MqttListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * topic网关自定义监听类
 *
 * @author Yang
 */
@Slf4j
@AllArgsConstructor
public class CustomMqttListener implements MqttListener<String> {
    private final JsConvertService jsConvertService;

    @Override
    public void onMessage(String topic, String msgStr) {
        jsConvertService.upHandle(topic, msgStr);
    }
}

