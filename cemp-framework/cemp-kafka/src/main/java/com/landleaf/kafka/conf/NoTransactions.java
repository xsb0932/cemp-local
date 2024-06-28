package com.landleaf.kafka.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class NoTransactions extends Base {
    @Bean
    public Consumer<List<String>> consumer() {
        return list -> list.forEach(str -> bridge.send("consumer-in-0", str.toUpperCase()));
    }
}
