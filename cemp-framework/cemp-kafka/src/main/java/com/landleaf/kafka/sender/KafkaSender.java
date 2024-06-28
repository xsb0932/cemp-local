package com.landleaf.kafka.sender;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaSender {

    @Resource
    private KafkaTemplate<byte[], byte[]> template;

    public CompletableFuture<SendResult<byte[], byte[]>> send (String topic, String msg) {
        return template.send(topic, msg.getBytes());
    }
}
