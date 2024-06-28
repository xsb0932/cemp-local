package com.landleaf.kafka.conf;

import jakarta.annotation.Resource;
import org.springframework.cloud.stream.function.StreamBridge;

public class Base {
    @Resource
    protected StreamBridge bridge;
}
