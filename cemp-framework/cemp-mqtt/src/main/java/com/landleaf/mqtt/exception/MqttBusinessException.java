package com.landleaf.mqtt.exception;

/**
 * @author lokiy
 * @date 2022/12/21
 * @description mqtt业务一场类
 */
public class MqttBusinessException extends RuntimeException{

    public MqttBusinessException(String message) {
        super(message);
    }
}
